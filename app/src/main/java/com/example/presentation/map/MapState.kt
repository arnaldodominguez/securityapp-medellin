package com.example.presentation.map

import com.example.core.bluetooth.BleConnectionState
import com.example.core.location.GpsStatus
import com.example.core.location.ZoneColorClassifier
import com.example.core.location.ZoneDangerColor
import com.example.core.location.ZoneEvaluation
import com.example.data.model.ComunaZone
import com.example.data.model.MarkerType
import com.example.data.model.SecurityLevel
import com.example.data.model.TacticalMarker

data class MapState(
    val comunas: List<ComunaZone> = emptyList(),
    val markers: List<TacticalMarker> = emptyList(),
    val activeComuna: ComunaZone? = null,
    val selectedFilterLevel: SecurityLevel? = null,
    val selectedMarkerFilter: MarkerType? = null,
    val isGeofenceActive: Boolean = true,
    val showProtocolsSheet: Boolean = false,
    val showSosConfirmationDialog: Boolean = false,
    val sosDispatchedMessage: String? = null,
    val gpsStatus: GpsStatus = GpsStatus.Searching,
    val zoneEvaluation: ZoneEvaluation = ZoneEvaluation(
        dangerColor = ZoneDangerColor.NO_GPS,
        detectedComuna = null,
        isGpsActive = false,
        signalCodeToSend = 4
    ),
    val bleConnectionState: BleConnectionState = BleConnectionState.Disconnected
)
