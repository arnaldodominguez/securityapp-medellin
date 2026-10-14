package com.example.data.model

data class ComunaZone(
    val id: Int,
    val name: String,
    val sectorName: String,
    val securityLevel: SecurityLevel,
    val riskScore: Int,
    val activeQuadrant: String,
    val quadrantPhone: String,
    val siscCommerceChange: String,
    val siscPedestrianChange: String,
    val activeReportsCount: Int,
    val policePatrolsCount: Int,
    val camerasCount: Int,
    val latitude: Double,
    val longitude: Double,
    // Relative visual position for tactical cartography grid (X: 0.0..1.0 west-east, Y: 0.0..1.0 north-south in Medellín valley)
    val mapNormX: Float,
    val mapNormY: Float,
    val polygonNormPoints: List<Pair<Float, Float>> = emptyList()
)

enum class MarkerType {
    POLICE_PATROL,
    CAMERA_123,
    INCIDENT_REPORT,
    CAI_STATION
}

data class TacticalMarker(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: MarkerType,
    val comunaId: Int,
    val normX: Float,
    val normY: Float,
    val latitude: Double,
    val longitude: Double,
    val securityLevel: SecurityLevel? = null
)

data class ZoneAlertEntity(
    val activeComuna: ComunaZone,
    val timestamp: Long = System.currentTimeMillis(),
    val isGeofenceActive: Boolean = true,
    val nearbyAlertsCount: Int = 3
)
