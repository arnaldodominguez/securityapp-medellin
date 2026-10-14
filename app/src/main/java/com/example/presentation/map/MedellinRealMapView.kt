package com.example.presentation.map

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.core.bluetooth.BleConnectionState
import com.example.core.location.GpsStatus
import com.example.core.location.ZoneColorClassifier
import com.example.core.location.ZoneDangerColor
import com.example.core.location.ZoneEvaluation
import com.example.data.model.ComunaZone
import com.example.ui.theme.FigmaBorder
import com.example.ui.theme.FigmaSurface
import com.example.ui.theme.FigmaTealPrimary
import com.example.ui.theme.FigmaTextPrimary
import com.example.ui.theme.FigmaTextSecondary
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Marker

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MedellinRealMapView(
    comunas: List<ComunaZone>,
    activeComuna: ComunaZone?,
    gpsStatus: GpsStatus,
    zoneEvaluation: ZoneEvaluation,
    bleConnectionState: BleConnectionState,
    onSelectComuna: (ComunaZone) -> Unit,
    onReconnectBle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }
    var currentTileSourceIndex by remember { mutableStateOf(0) }

    val tileSources = listOf(
        TileSourceFactory.MAPNIK,
        TileSourceFactory.USGS_TOPO
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // ==========================================================
        // 1. STATUS BAR: GPS & ESP32-C3 TELEMETRY
        // ==========================================================
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = FigmaSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val gpsIcon = if (gpsStatus is GpsStatus.Locked) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed
                        val gpsColor = when (zoneEvaluation.dangerColor) {
                            ZoneDangerColor.RED -> Color(0xFFEF4444)
                            ZoneDangerColor.YELLOW -> Color(0xFFF59E0B)
                            ZoneDangerColor.GREEN -> Color(0xFF10B981)
                            ZoneDangerColor.NO_GPS -> Color(0xFF64748B)
                        }

                        Icon(
                            imageVector = gpsIcon,
                            contentDescription = null,
                            tint = gpsColor,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = when (gpsStatus) {
                                is GpsStatus.Locked -> "GPS: ${zoneEvaluation.detectedComuna?.sectorName ?: "Medellín"}"
                                is GpsStatus.Searching -> "GPS: Buscando señal satelital..."
                                is GpsStatus.NoSignal -> "GPS: Sin señal satelital"
                                is GpsStatus.PermissionRequired -> "GPS: Permisos requeridos"
                            },
                            color = FigmaTextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = zoneEvaluation.dangerColor.color.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, zoneEvaluation.dangerColor.color)
                    ) {
                        Text(
                            text = "SEÑAL: ${zoneEvaluation.signalCodeToSend} • ${zoneEvaluation.dangerColor.label.take(8)}",
                            color = zoneEvaluation.dangerColor.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val bleIcon = when (bleConnectionState) {
                            is BleConnectionState.Connected -> Icons.Default.BluetoothConnected
                            is BleConnectionState.Scanning, is BleConnectionState.Connecting -> Icons.Default.Bluetooth
                            else -> Icons.Default.BluetoothDisabled
                        }
                        val bleColor = when (bleConnectionState) {
                            is BleConnectionState.Connected -> Color(0xFF10B981)
                            is BleConnectionState.Connecting, is BleConnectionState.Scanning -> Color(0xFF0284C7)
                            else -> Color(0xFFEF4444)
                        }

                        Icon(
                            imageVector = bleIcon,
                            contentDescription = null,
                            tint = bleColor,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            text = when (bleConnectionState) {
                                is BleConnectionState.Connected -> "ESP32-C3: Conectado (Enviando '${zoneEvaluation.signalCodeToSend}')"
                                is BleConnectionState.Connecting -> "ESP32-C3: Enlazando brazalete..."
                                is BleConnectionState.Scanning -> "ESP32-C3: Escaneando brazalete..."
                                is BleConnectionState.Disconnected -> "ESP32-C3: No conectado"
                                is BleConnectionState.Failed -> "ESP32-C3: Desconectado"
                            },
                            color = if (bleConnectionState is BleConnectionState.Connected) Color(0xFF0F766E) else FigmaTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (bleConnectionState is BleConnectionState.Connected) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    if (bleConnectionState !is BleConnectionState.Connected) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FigmaTealPrimary,
                            modifier = Modifier.clickable { onReconnectBle() }
                        ) {
                            Text(
                                text = "Conectar",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================================
        // 2. NATIVE OSMDROID MAP VIEW (Real Streets, Buildings & Details + Risk Zones Overlay)
        // ==========================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.2.dp, FigmaBorder, RoundedCornerShape(22.dp))
                .background(Color(0xFFE2EFEF))
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                    Configuration.getInstance().userAgentValue = "SecurityApp-Medellin/1.0"

                    MapView(ctx).apply {
                        setTileSource(tileSources[currentTileSourceIndex])
                        setMultiTouchControls(true)
                        controller.setZoom(13.5)
                        controller.setCenter(GeoPoint(6.2476, -75.5750))

                        // Render 16 Comunas Polygons on top of map streets
                        comunas.forEach { comuna ->
                            val danger = ZoneColorClassifier.getComunaDangerColor(comuna)
                            val perimeter = ZoneColorClassifier.getComunaPerimeter(comuna.id, comuna.latitude, comuna.longitude)
                            
                            val polygon = Polygon().apply {
                                points = perimeter.map { (lat, lng) -> GeoPoint(lat, lng) }
                                val alphaColor = danger.color
                                fillColor = android.graphics.Color.argb(
                                    45, // Translucidez optimizada para ver calles y detalles de la ciudad debajo
                                    (alphaColor.red * 255).toInt(),
                                    (alphaColor.green * 255).toInt(),
                                    (alphaColor.blue * 255).toInt()
                                )
                                strokeColor = android.graphics.Color.argb(
                                    255,
                                    (alphaColor.red * 255).toInt(),
                                    (alphaColor.green * 255).toInt(),
                                    (alphaColor.blue * 255).toInt()
                                )
                                strokeWidth = 3.8f
                                title = "${comuna.name} (${comuna.sectorName})\nRiesgo: ${comuna.riskScore}%"
                                setOnClickListener { _, _, _ ->
                                    onSelectComuna(comuna)
                                    true
                                }
                            }
                            overlays.add(polygon)
                        }

                        mapViewInstance = this
                    }
                },
                update = { mapView ->
                    mapViewInstance = mapView
                    mapView.overlays.removeAll { it is Marker }
                    if (gpsStatus is GpsStatus.Locked) {
                        val gpsPoint = GeoPoint(gpsStatus.latitude, gpsStatus.longitude)
                        val marker = Marker(mapView).apply {
                            position = gpsPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = "Mi Ubicación GPS"
                        }
                        mapView.overlays.add(marker)
                        mapView.invalidate()
                    }
                }
            )

            // MAP FLOATING ACTION BUTTONS
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Zoom In
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { mapViewInstance?.controller?.zoomIn() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = FigmaTealPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                // Zoom Out
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { mapViewInstance?.controller?.zoomOut() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = FigmaTealPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                // Cambiar Capa
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            currentTileSourceIndex = (currentTileSourceIndex + 1) % tileSources.size
                            mapViewInstance?.setTileSource(tileSources[currentTileSourceIndex])
                            mapViewInstance?.invalidate()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Layers, contentDescription = "Cambiar Capa", tint = FigmaTealPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                // Recenter GPS / Medellín
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FigmaBorder),
                    modifier = Modifier
                        .size(42.dp)
                        .clickable {
                            if (gpsStatus is GpsStatus.Locked) {
                                mapViewInstance?.controller?.animateTo(GeoPoint(gpsStatus.latitude, gpsStatus.longitude), 15.0, 600L)
                                zoneEvaluation.detectedComuna?.let { onSelectComuna(it) }
                            } else {
                                mapViewInstance?.controller?.animateTo(GeoPoint(6.2476, -75.5750), 13.5, 600L)
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Mi Ubicación GPS",
                            tint = FigmaTealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Attribution badge
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color(0xDDFFFFFF),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FigmaBorder)
            ) {
                Text(
                    text = "© OpenStreetMap • Medellín",
                    color = Color(0xFF334155),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
