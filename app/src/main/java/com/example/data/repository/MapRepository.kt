package com.example.data.repository

import com.example.data.model.ComunaZone
import com.example.data.model.MarkerType
import com.example.data.model.SecurityLevel
import com.example.data.model.TacticalMarker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface MapRepository {
    fun getComunas(): Flow<List<ComunaZone>>
    fun getMarkers(): Flow<List<TacticalMarker>>
    fun getActiveComuna(): Flow<ComunaZone>
    fun setActiveComuna(comunaId: Int)
}

class MapRepositoryImpl : MapRepository {

    private val comunasData = listOf(
        ComunaZone(
            id = 1,
            name = "Comuna 1 — Popular",
            sectorName = "Popular",
            securityLevel = SecurityLevel.LEVEL_4,
            riskScore = 52,
            activeQuadrant = "01-1-2",
            quadrantPhone = "123",
            siscCommerceChange = "-6% este mes",
            siscPedestrianChange = "-2% incidentes",
            activeReportsCount = 6,
            policePatrolsCount = 4,
            camerasCount = 18,
            latitude = 6.2985,
            longitude = -75.5460,
            mapNormX = 0.68f,
            mapNormY = 0.16f
        ),
        ComunaZone(
            id = 2,
            name = "Comuna 2 — Santa Cruz",
            sectorName = "Santa Cruz",
            securityLevel = SecurityLevel.LEVEL_3,
            riskScore = 38,
            activeQuadrant = "02-1-4",
            quadrantPhone = "123",
            siscCommerceChange = "-9% este mes",
            siscPedestrianChange = "-5% incidentes",
            activeReportsCount = 4,
            policePatrolsCount = 3,
            camerasCount = 14,
            latitude = 6.2940,
            longitude = -75.5600,
            mapNormX = 0.55f,
            mapNormY = 0.18f
        ),
        ComunaZone(
            id = 3,
            name = "Comuna 3 — Manrique",
            sectorName = "Manrique",
            securityLevel = SecurityLevel.LEVEL_4,
            riskScore = 49,
            activeQuadrant = "03-2-1",
            quadrantPhone = "123",
            siscCommerceChange = "-4% este mes",
            siscPedestrianChange = "-1% incidentes",
            activeReportsCount = 8,
            policePatrolsCount = 5,
            camerasCount = 22,
            latitude = 6.2750,
            longitude = -75.5480,
            mapNormX = 0.65f,
            mapNormY = 0.28f
        ),
        ComunaZone(
            id = 4,
            name = "Comuna 4 — Aranjuez",
            sectorName = "Aranjuez",
            securityLevel = SecurityLevel.LEVEL_6,
            riskScore = 76,
            activeQuadrant = "04-1-3",
            quadrantPhone = "123",
            siscCommerceChange = "+11% este mes",
            siscPedestrianChange = "+15% incidentes",
            activeReportsCount = 14,
            policePatrolsCount = 7,
            camerasCount = 31,
            latitude = 6.2780,
            longitude = -75.5620,
            mapNormX = 0.52f,
            mapNormY = 0.26f
        ),
        ComunaZone(
            id = 5,
            name = "Comuna 5 — Castilla",
            sectorName = "Castilla",
            securityLevel = SecurityLevel.LEVEL_5,
            riskScore = 62,
            activeQuadrant = "05-2-2",
            quadrantPhone = "123",
            siscCommerceChange = "+3% este mes",
            siscPedestrianChange = "+7% incidentes",
            activeReportsCount = 11,
            policePatrolsCount = 6,
            camerasCount = 26,
            latitude = 6.2920,
            longitude = -75.5780,
            mapNormX = 0.38f,
            mapNormY = 0.20f
        ),
        ComunaZone(
            id = 6,
            name = "Comuna 6 — Doce de Octubre",
            sectorName = "Doce de Octubre",
            securityLevel = SecurityLevel.LEVEL_4,
            riskScore = 48,
            activeQuadrant = "06-1-1",
            quadrantPhone = "123",
            siscCommerceChange = "-2% este mes",
            siscPedestrianChange = "-3% incidentes",
            activeReportsCount = 5,
            policePatrolsCount = 4,
            camerasCount = 19,
            latitude = 6.2990,
            longitude = -75.5890,
            mapNormX = 0.28f,
            mapNormY = 0.17f
        ),
        ComunaZone(
            id = 7,
            name = "Comuna 7 — Robledo",
            sectorName = "Robledo",
            securityLevel = SecurityLevel.LEVEL_3,
            riskScore = 35,
            activeQuadrant = "07-3-2",
            quadrantPhone = "123",
            siscCommerceChange = "-12% este mes",
            siscPedestrianChange = "-8% incidentes",
            activeReportsCount = 7,
            policePatrolsCount = 5,
            camerasCount = 29,
            latitude = 6.2740,
            longitude = -75.5960,
            mapNormX = 0.24f,
            mapNormY = 0.32f
        ),
        ComunaZone(
            id = 8,
            name = "Comuna 8 — Villa Hermosa",
            sectorName = "Villa Hermosa",
            securityLevel = SecurityLevel.LEVEL_4,
            riskScore = 51,
            activeQuadrant = "08-2-4",
            quadrantPhone = "123",
            siscCommerceChange = "-3% este mes",
            siscPedestrianChange = "+2% incidentes",
            activeReportsCount = 6,
            policePatrolsCount = 4,
            camerasCount = 21,
            latitude = 6.2550,
            longitude = -75.5450,
            mapNormX = 0.69f,
            mapNormY = 0.44f
        ),
        ComunaZone(
            id = 9,
            name = "Comuna 9 — Buenos Aires",
            sectorName = "Buenos Aires",
            securityLevel = SecurityLevel.LEVEL_3,
            riskScore = 39,
            activeQuadrant = "09-1-2",
            quadrantPhone = "123",
            siscCommerceChange = "-8% este mes",
            siscPedestrianChange = "-4% incidentes",
            activeReportsCount = 5,
            policePatrolsCount = 4,
            camerasCount = 23,
            latitude = 6.2390,
            longitude = -75.5530,
            mapNormX = 0.64f,
            mapNormY = 0.54f
        ),
        ComunaZone(
            id = 10,
            name = "Comuna 10 — La Candelaria (Centro)",
            sectorName = "Centro",
            securityLevel = SecurityLevel.LEVEL_5,
            riskScore = 68,
            activeQuadrant = "10-2-4",
            quadrantPhone = "123",
            siscCommerceChange = "Hurto comercio -14%",
            siscPedestrianChange = "Cuadrante activo: 10-2-4",
            activeReportsCount = 23,
            policePatrolsCount = 12,
            camerasCount = 64,
            latitude = 6.2518,
            longitude = -75.5636,
            mapNormX = 0.52f,
            mapNormY = 0.46f
        ),
        ComunaZone(
            id = 11,
            name = "Comuna 11 — Laureles - Estadio",
            sectorName = "Laureles",
            securityLevel = SecurityLevel.LEVEL_2,
            riskScore = 22,
            activeQuadrant = "11-1-3",
            quadrantPhone = "123",
            siscCommerceChange = "-18% este mes",
            siscPedestrianChange = "-12% incidentes",
            activeReportsCount = 3,
            policePatrolsCount = 8,
            camerasCount = 42,
            latitude = 6.2480,
            longitude = -75.5890,
            mapNormX = 0.35f,
            mapNormY = 0.48f
        ),
        ComunaZone(
            id = 12,
            name = "Comuna 12 — La América",
            sectorName = "La América",
            securityLevel = SecurityLevel.LEVEL_2,
            riskScore = 24,
            activeQuadrant = "12-2-1",
            quadrantPhone = "123",
            siscCommerceChange = "-15% este mes",
            siscPedestrianChange = "-10% incidentes",
            activeReportsCount = 3,
            policePatrolsCount = 5,
            camerasCount = 28,
            latitude = 6.2530,
            longitude = -75.6020,
            mapNormX = 0.22f,
            mapNormY = 0.45f
        ),
        ComunaZone(
            id = 13,
            name = "Comuna 13 — San Javier",
            sectorName = "San Javier",
            securityLevel = SecurityLevel.LEVEL_4,
            riskScore = 45,
            activeQuadrant = "13-3-1",
            quadrantPhone = "123",
            siscCommerceChange = "-5% este mes",
            siscPedestrianChange = "-6% incidentes",
            activeReportsCount = 7,
            policePatrolsCount = 6,
            camerasCount = 35,
            latitude = 6.2520,
            longitude = -75.6170,
            mapNormX = 0.12f,
            mapNormY = 0.46f
        ),
        ComunaZone(
            id = 14,
            name = "Comuna 14 — El Poblado",
            sectorName = "El Poblado",
            securityLevel = SecurityLevel.LEVEL_3,
            riskScore = 34,
            activeQuadrant = "14-2-5",
            quadrantPhone = "123",
            siscCommerceChange = "-7% este mes",
            siscPedestrianChange = "+4% nocturno",
            activeReportsCount = 9,
            policePatrolsCount = 10,
            camerasCount = 58,
            latitude = 6.2080,
            longitude = -75.5670,
            mapNormX = 0.56f,
            mapNormY = 0.74f
        ),
        ComunaZone(
            id = 15,
            name = "Comuna 15 — Guayabal",
            sectorName = "Guayabal",
            securityLevel = SecurityLevel.LEVEL_3,
            riskScore = 37,
            activeQuadrant = "15-1-2",
            quadrantPhone = "123",
            siscCommerceChange = "-11% este mes",
            siscPedestrianChange = "-5% incidentes",
            activeReportsCount = 4,
            policePatrolsCount = 5,
            camerasCount = 24,
            latitude = 6.2160,
            longitude = -75.5860,
            mapNormX = 0.40f,
            mapNormY = 0.69f
        ),
        ComunaZone(
            id = 16,
            name = "Comuna 16 — Belén",
            sectorName = "Belén",
            securityLevel = SecurityLevel.LEVEL_2,
            riskScore = 26,
            activeQuadrant = "16-2-3",
            quadrantPhone = "123",
            siscCommerceChange = "-14% este mes",
            siscPedestrianChange = "-9% incidentes",
            activeReportsCount = 4,
            policePatrolsCount = 7,
            camerasCount = 36,
            latitude = 6.2280,
            longitude = -75.5980,
            mapNormX = 0.28f,
            mapNormY = 0.65f
        ),
        ComunaZone(
            id = 50,
            name = "Corregimiento — San Sebastián de Palmitas",
            sectorName = "Palmitas",
            securityLevel = SecurityLevel.LEVEL_1,
            riskScore = 15,
            activeQuadrant = "50-1-1",
            quadrantPhone = "123",
            siscCommerceChange = "-20% este mes",
            siscPedestrianChange = "-15% incidentes",
            activeReportsCount = 1,
            policePatrolsCount = 2,
            camerasCount = 5,
            latitude = 6.3000,
            longitude = -75.6800,
            mapNormX = 0.10f,
            mapNormY = 0.15f
        ),
        ComunaZone(
            id = 60,
            name = "Corregimiento — San Cristóbal",
            sectorName = "San Cristóbal",
            securityLevel = SecurityLevel.LEVEL_2,
            riskScore = 28,
            activeQuadrant = "60-1-2",
            quadrantPhone = "123",
            siscCommerceChange = "-10% este mes",
            siscPedestrianChange = "-8% incidentes",
            activeReportsCount = 3,
            policePatrolsCount = 3,
            camerasCount = 10,
            latitude = 6.2700,
            longitude = -75.6400,
            mapNormX = 0.15f,
            mapNormY = 0.35f
        ),
        ComunaZone(
            id = 70,
            name = "Corregimiento — Altavista",
            sectorName = "Altavista",
            securityLevel = SecurityLevel.LEVEL_3,
            riskScore = 32,
            activeQuadrant = "70-2-1",
            quadrantPhone = "123",
            siscCommerceChange = "-8% este mes",
            siscPedestrianChange = "-4% incidentes",
            activeReportsCount = 4,
            policePatrolsCount = 3,
            camerasCount = 12,
            latitude = 6.2200,
            longitude = -75.6300,
            mapNormX = 0.18f,
            mapNormY = 0.58f
        ),
        ComunaZone(
            id = 80,
            name = "Corregimiento — San Antonio de Prado",
            sectorName = "San Antonio de Prado",
            securityLevel = SecurityLevel.LEVEL_2,
            riskScore = 29,
            activeQuadrant = "80-1-3",
            quadrantPhone = "123",
            siscCommerceChange = "-12% este mes",
            siscPedestrianChange = "-7% incidentes",
            activeReportsCount = 3,
            policePatrolsCount = 4,
            camerasCount = 15,
            latitude = 6.1700,
            longitude = -75.6300,
            mapNormX = 0.20f,
            mapNormY = 0.85f
        ),
        ComunaZone(
            id = 90,
            name = "Corregimiento — Santa Elena",
            sectorName = "Santa Elena",
            securityLevel = SecurityLevel.LEVEL_1,
            riskScore = 18,
            activeQuadrant = "90-3-1",
            quadrantPhone = "123",
            siscCommerceChange = "-22% este mes",
            siscPedestrianChange = "-18% incidentes",
            activeReportsCount = 1,
            policePatrolsCount = 3,
            camerasCount = 8,
            latitude = 6.2500,
            longitude = -75.5000,
            mapNormX = 0.85f,
            mapNormY = 0.35f
        )
    )

    private val markersData = listOf(
        TacticalMarker(
            id = "patrol-1",
            title = "Patrulla Cuadrante #10",
            subtitle = "Móvil M-104 en San Juan con Palacé",
            type = MarkerType.POLICE_PATROL,
            comunaId = 10,
            normX = 0.50f,
            normY = 0.44f,
            latitude = 6.2510,
            longitude = -75.5650
        ),
        TacticalMarker(
            id = "patrol-2",
            title = "Patrulla Cuadrante #4",
            subtitle = "Móvil M-42 en Parque Aranjuez",
            type = MarkerType.POLICE_PATROL,
            comunaId = 4,
            normX = 0.53f,
            normY = 0.24f,
            latitude = 6.2790,
            longitude = -75.5610
        ),
        TacticalMarker(
            id = "patrol-3",
            title = "Patrulla Cuadrante #14",
            subtitle = "Móvil M-145 en Parque Lleras",
            type = MarkerType.POLICE_PATROL,
            comunaId = 14,
            normX = 0.58f,
            normY = 0.72f,
            latitude = 6.2090,
            longitude = -75.5650
        ),
        TacticalMarker(
            id = "cam-1",
            title = "Domo 123 Medellín #C-102",
            subtitle = "PTZ 4K Zoom 360° Operativo CAD",
            type = MarkerType.CAMERA_123,
            comunaId = 10,
            normX = 0.54f,
            normY = 0.48f,
            latitude = 6.2525,
            longitude = -75.5620
        ),
        TacticalMarker(
            id = "cam-2",
            title = "Cámara LPR Lectura Placas",
            subtitle = "Av. Oriental con La Playa",
            type = MarkerType.CAMERA_123,
            comunaId = 10,
            normX = 0.51f,
            normY = 0.47f,
            latitude = 6.2490,
            longitude = -75.5640
        ),
        TacticalMarker(
            id = "cam-3",
            title = "Domo 123 Metroplús Aranjuez",
            subtitle = "Monitoreo perimetral estación",
            type = MarkerType.CAMERA_123,
            comunaId = 4,
            normX = 0.51f,
            normY = 0.28f,
            latitude = 6.2770,
            longitude = -75.5630
        ),
        TacticalMarker(
            id = "rep-1",
            title = "Hurto en parada Metroplús",
            subtitle = "Reporte nivel 6 validado por 14 vecinos",
            type = MarkerType.INCIDENT_REPORT,
            comunaId = 4,
            normX = 0.53f,
            normY = 0.27f,
            latitude = 6.2785,
            longitude = -75.5615,
            securityLevel = SecurityLevel.LEVEL_6
        ),
        TacticalMarker(
            id = "rep-2",
            title = "Luminaria averiada y baja luz",
            subtitle = "Calle 10 con Cra 43A El Poblado",
            type = MarkerType.INCIDENT_REPORT,
            comunaId = 14,
            normX = 0.55f,
            normY = 0.75f,
            latitude = 6.2075,
            longitude = -75.5680,
            securityLevel = SecurityLevel.LEVEL_3
        )
    )

    private val activeComunaFlow = MutableStateFlow(comunasData.first { it.id == 10 }) // Default Comuna 10 (La Candelaria)
    private val comunasFlow = MutableStateFlow(comunasData)
    private val markersFlow = MutableStateFlow(markersData)

    override fun getComunas(): Flow<List<ComunaZone>> = comunasFlow.asStateFlow()

    override fun getMarkers(): Flow<List<TacticalMarker>> = markersFlow.asStateFlow()

    override fun getActiveComuna(): Flow<ComunaZone> = activeComunaFlow.asStateFlow()

    override fun setActiveComuna(comunaId: Int) {
        val found = comunasData.firstOrNull { it.id == comunaId }
        if (found != null) {
            activeComunaFlow.value = found
        }
    }
}
