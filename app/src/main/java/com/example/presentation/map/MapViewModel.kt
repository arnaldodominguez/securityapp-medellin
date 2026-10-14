package com.example.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.bluetooth.BleConnectionState
import com.example.core.bluetooth.Esp32BleManager
import com.example.core.location.GpsLocationTracker
import com.example.core.location.GpsStatus
import com.example.core.location.ZoneColorClassifier
import com.example.core.location.ZoneDangerColor
import com.example.core.location.ZoneEvaluation
import com.example.data.model.ComunaZone
import com.example.data.model.MarkerType
import com.example.data.model.SecurityLevel
import com.example.data.repository.AuthRepository
import com.example.data.repository.MapRepository
import com.example.domain.usecase.TriggerSosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapViewModel(
    private val mapRepository: MapRepository,
    private val authRepository: AuthRepository,
    private val triggerSosUseCase: TriggerSosUseCase = TriggerSosUseCase(),
    private val gpsTracker: GpsLocationTracker? = null,
    private val bleManager: Esp32BleManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    init {
        mapRepository.getComunas().onEach { list ->
            _uiState.value = _uiState.value.copy(comunas = list)
            reevaluateGps()
        }.launchIn(viewModelScope)

        mapRepository.getMarkers().onEach { markers ->
            _uiState.value = _uiState.value.copy(markers = markers)
        }.launchIn(viewModelScope)

        mapRepository.getActiveComuna().onEach { active ->
            if (_uiState.value.gpsStatus !is GpsStatus.Locked) {
                _uiState.value = _uiState.value.copy(activeComuna = active)
            }
        }.launchIn(viewModelScope)

        // Observe Live GPS Updates
        gpsTracker?.gpsStatus?.onEach { status ->
            _uiState.value = _uiState.value.copy(gpsStatus = status)
            reevaluateGps()
        }?.launchIn(viewModelScope)

        // Observe BLE Connection to ESP32-C3
        bleManager?.connectionState?.onEach { bleState ->
            _uiState.value = _uiState.value.copy(bleConnectionState = bleState)
        }?.launchIn(viewModelScope)
    }

    fun startTrackingAndBle() {
        gpsTracker?.startTracking()
        bleManager?.startScanAndAutoConnect()
    }

    fun reconnectBle() {
        bleManager?.startScanAndAutoConnect()
    }

    private fun reevaluateGps() {
        val status = _uiState.value.gpsStatus
        val comunas = _uiState.value.comunas

        val evaluation: ZoneEvaluation = when (status) {
            is GpsStatus.Locked -> {
                ZoneColorClassifier.evaluateGpsPosition(
                    latitude = status.latitude,
                    longitude = status.longitude,
                    comunas = comunas
                )
            }
            is GpsStatus.NoSignal, is GpsStatus.Searching, is GpsStatus.PermissionRequired -> {
                ZoneEvaluation(
                    dangerColor = ZoneDangerColor.NO_GPS,
                    detectedComuna = null,
                    isGpsActive = false,
                    signalCodeToSend = 4
                )
            }
        }

        _uiState.value = _uiState.value.copy(
            zoneEvaluation = evaluation,
            activeComuna = evaluation.detectedComuna ?: _uiState.value.activeComuna
        )

        // Transmit Signal Code to ESP32-C3 mini (1=ROJO, 2=AMARILLO, 3=VERDE, 4=SIN GPS)
        bleManager?.sendSignal(evaluation.signalCodeToSend)
    }

    fun selectComuna(comuna: ComunaZone) {
        mapRepository.setActiveComuna(comuna.id)
        val dangerColor = ZoneColorClassifier.getComunaDangerColor(comuna)
        val manualEval = ZoneEvaluation(
            dangerColor = dangerColor,
            detectedComuna = comuna,
            isGpsActive = _uiState.value.gpsStatus is GpsStatus.Locked,
            signalCodeToSend = dangerColor.code
        )
        _uiState.value = _uiState.value.copy(
            activeComuna = comuna,
            zoneEvaluation = manualEval
        )
        bleManager?.sendSignal(dangerColor.code)
    }

    fun simulateGpsLocation(lat: Double, lng: Double) {
        gpsTracker?.setManualLocation(lat, lng)
    }

    fun simulateNoGps() {
        gpsTracker?.simulateNoSignal()
    }

    fun filterBySecurityLevel(level: SecurityLevel?) {
        _uiState.value = _uiState.value.copy(
            selectedFilterLevel = if (_uiState.value.selectedFilterLevel == level) null else level
        )
    }

    fun filterMarkerType(type: MarkerType?) {
        _uiState.value = _uiState.value.copy(
            selectedMarkerFilter = if (_uiState.value.selectedMarkerFilter == type) null else type
        )
    }

    fun toggleGeofence() {
        _uiState.value = _uiState.value.copy(
            isGeofenceActive = !_uiState.value.isGeofenceActive
        )
    }

    fun openProtocols() {
        _uiState.value = _uiState.value.copy(showProtocolsSheet = true)
    }

    fun closeProtocols() {
        _uiState.value = _uiState.value.copy(showProtocolsSheet = false)
    }

    fun requestSos() {
        _uiState.value = _uiState.value.copy(showSosConfirmationDialog = true)
    }

    fun dismissSosDialog() {
        _uiState.value = _uiState.value.copy(showSosConfirmationDialog = false)
    }

    fun confirmSos() {
        viewModelScope.launch {
            val comuna = _uiState.value.activeComuna ?: return@launch
            val profile = authRepository.getUserProfile().first()
            val sosResult = triggerSosUseCase(comuna, profile)
            _uiState.value = _uiState.value.copy(
                showSosConfirmationDialog = false,
                sosDispatchedMessage = "¡Despacho SOS transmitido a Línea 123 y cuadrante ${sosResult.activeQuadrant}! SMS enviado a ${sosResult.smsRecipientName}."
            )
        }
    }

    fun clearSosMessage() {
        _uiState.value = _uiState.value.copy(sosDispatchedMessage = null)
    }
}
