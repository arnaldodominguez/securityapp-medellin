package com.example.core.bluetooth

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

sealed class BleConnectionState {
    object Disconnected : BleConnectionState()
    object Scanning : BleConnectionState()
    data class Connecting(val deviceName: String) : BleConnectionState()
    data class Connected(val deviceName: String, val lastSignal: Int) : BleConnectionState()
    data class Failed(val reason: String) : BleConnectionState()
}

class Esp32BleManager(private val context: Context) {
    companion object {
        private const val TAG = "Esp32BleManager"
        private const val NOTIFICATION_CHANNEL_ID = "esp32_bracelet_channel"
        private const val NOTIFICATION_ID = 2001

        // Standard Nordic UART / Custom Serial BLE UUIDs for ESP32-C3
        val SERVICE_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val RX_CHAR_UUID: UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    }

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private val scanner: BluetoothLeScanner? get() = bluetoothAdapter?.bluetoothLeScanner
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val _connectionState = MutableStateFlow<BleConnectionState>(BleConnectionState.Disconnected)
    val connectionState: StateFlow<BleConnectionState> = _connectionState.asStateFlow()

    private val _lastSentCode = MutableStateFlow(4)
    val lastSentCode: StateFlow<Int> = _lastSentCode.asStateFlow()

    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var connectedDevice: BluetoothDevice? = null
    private var isScanning = false
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Estado del Brazalete ESP32",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas de conectividad con el brazalete ESP32-C3"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasBlePermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scan = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            val connect = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            return scan && connect
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun startScanAndAutoConnect() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _connectionState.value = BleConnectionState.Failed("Bluetooth desactivado")
            notifyDisconnectedAlert("El Bluetooth está apagado en tu teléfono.")
            return
        }

        if (!hasBlePermissions()) {
            _connectionState.value = BleConnectionState.Failed("Permisos de Bluetooth requeridos")
            return
        }

        if (isScanning) return
        isScanning = true
        _connectionState.value = BleConnectionState.Scanning

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            scanner?.startScan(null, scanSettings, leScanCallback)

            // Stop scan after 15 seconds if not found
            mainHandler.postDelayed({
                if (isScanning && _connectionState.value is BleConnectionState.Scanning) {
                    stopScan()
                    _connectionState.value = BleConnectionState.Failed("No se encontró el brazalete ESP32")
                    notifyDisconnectedAlert("No se logró comunicación con el brazalete ESP32-C3.")
                }
            }, 15000L)
        } catch (e: Exception) {
            Log.e(TAG, "Error iniciando escaneo BLE", e)
            _connectionState.value = BleConnectionState.Failed(e.message ?: "Error de escaneo")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (isScanning) {
            isScanning = false
            try {
                scanner?.stopScan(leScanCallback)
            } catch (_: Exception) {}
        }
    }

    private val leScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                val name = device.name ?: ""
                val address = device.address ?: ""

                // Match ESP32 names or open match
                val isEsp32 = name.contains("ESP32", ignoreCase = true) ||
                        name.contains("BRAZALETE", ignoreCase = true) ||
                        name.contains("C3", ignoreCase = true) ||
                        name.contains("SECURITY", ignoreCase = true)

                if (isEsp32 || (name.isNotEmpty() && isScanning)) {
                    stopScan()
                    connectGatt(device)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            isScanning = false
            _connectionState.value = BleConnectionState.Failed("Fallo en escaneo BLE ($errorCode)")
            notifyDisconnectedAlert("Fallo en el escáner Bluetooth del celular.")
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectGatt(device: BluetoothDevice) {
        val devName = device.name ?: "ESP32-C3 Mini"
        _connectionState.value = BleConnectionState.Connecting(devName)
        connectedDevice = device

        bluetoothGatt = device.connectGatt(
            context,
            false,
            gattCallback,
            BluetoothDevice.TRANSPORT_LE
        )
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val devName = connectedDevice?.name ?: "ESP32-C3 Mini"
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Conectado a GATT: $devName. Descubriendo servicios...")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w(TAG, "Desconectado de $devName")
                mainHandler.post {
                    _connectionState.value = BleConnectionState.Disconnected
                    writeCharacteristic = null
                    notifyDisconnectedAlert("Se perdió la comunicación efectiva con el brazalete ESP32.")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val devName = connectedDevice?.name ?: "ESP32-C3 Mini"
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Find writable characteristic
                for (service in gatt.services) {
                    for (charac in service.characteristics) {
                        val isWritable = (charac.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
                                (charac.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
                        if (charac.uuid == RX_CHAR_UUID || isWritable) {
                            writeCharacteristic = charac
                            break
                        }
                    }
                    if (writeCharacteristic != null) break
                }

                mainHandler.post {
                    _connectionState.value = BleConnectionState.Connected(devName, _lastSentCode.value)
                    // Transmit current signal code immediately on connect
                    sendSignal(_lastSentCode.value)
                }
            } else {
                mainHandler.post {
                    _connectionState.value = BleConnectionState.Failed("Fallo al descubrir servicios GATT")
                    notifyDisconnectedAlert("Fallo al sincronizar servicios del brazalete ESP32.")
                }
            }
        }
    }

    /**
     * Envia el código (1=Rojo, 2=Amarillo, 3=Verde, 4=Sin GPS) hacia el ESP32-C3.
     */
    @SuppressLint("MissingPermission")
    fun sendSignal(code: Int) {
        _lastSentCode.value = code
        val gatt = bluetoothGatt
        val charac = writeCharacteristic

        if (gatt == null || charac == null) {
            // Not connected yet, update state if connected
            return
        }

        val payload = "$code\n".toByteArray(Charsets.UTF_8)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(
                charac,
                payload,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        } else {
            @Suppress("DEPRECATION")
            charac.value = payload
            @Suppress("DEPRECATION")
            gatt.writeCharacteristic(charac)
        }

        connectedDevice?.let { dev ->
            val devName = dev.name ?: "ESP32-C3 Mini"
            _connectionState.value = BleConnectionState.Connected(devName, code)
        }
    }

    /**
     * Emite la notificación oficial de Android al usuario si no hay comunicación efectiva con el brazalete.
     */
    fun notifyDisconnectedAlert(message: String) {
        if (!hasBlePermissions()) return

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Alerta: Brazalete ESP32 Desconectado")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {}
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        stopScan()
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        } catch (_: Exception) {}
        bluetoothGatt = null
        writeCharacteristic = null
        _connectionState.value = BleConnectionState.Disconnected
    }
}
