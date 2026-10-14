package com.example.data.repository

import com.example.data.model.CommunityTabFilter
import com.example.data.model.DistanceRadiusFilter
import com.example.data.model.ReportEntity
import com.example.data.model.SecurityLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface ReportsRepository {
    fun getReports(tabFilter: CommunityTabFilter, radius: DistanceRadiusFilter): Flow<List<ReportEntity>>
    fun voteUseful(reportId: String)
    fun voteFalseAlarm(reportId: String)
    fun submitReport(
        title: String,
        description: String,
        comunaId: Int,
        comunaName: String,
        securityLevel: SecurityLevel,
        category: String,
        hasWitnessPhoto: Boolean
    )
}

class ReportsRepositoryImpl : ReportsRepository {

    private val initialReports = listOf(
        ReportEntity(
            id = "rep-001",
            title = "Hurto de celular en parada de Metroplús",
            description = "Dos sujetos en motocicleta abordaron a transeúnte amenazando con arma cortopunzante. Vecinos alertaron al cuadrante y se retiraron hacia el norte.",
            comunaId = 4,
            comunaName = "Parque Aranjuez • Comuna 4",
            address = "Estación Metroplús Aranjuez (Cra 49)",
            securityLevel = SecurityLevel.LEVEL_6,
            relativeTime = "Hace 12 minutos",
            validatedByNeighbors = 14,
            policeQuadrant = "Policía #4",
            usefulVotes = 28,
            falseAlarmVotes = 1,
            hasWitnessPhoto = true,
            photoUrl = null,
            category = "Hurto callejero",
            isOfficialBulletin = false,
            distanceMeters = 750,
            isUserContribution = false
        ),
        ReportEntity(
            id = "rep-002",
            title = "Avería de luminarias y zona oscura con aglomeración sospechosa",
            description = "Falla total en poste #10-44. Transeúntes reportan motociclistas sin placas estacionados en la esquina del parque lleras a baja iluminación.",
            comunaId = 14,
            comunaName = "Calle 10 con Cra 43A • El Poblado",
            address = "Calle 10 #43A-22",
            securityLevel = SecurityLevel.LEVEL_3,
            relativeTime = "Hace 35 minutos",
            validatedByNeighbors = 8,
            policeQuadrant = "EPM Alumbrado / Cuadrante 14-2",
            usefulVotes = 19,
            falseAlarmVotes = 0,
            hasWitnessPhoto = false,
            photoUrl = null,
            category = "Vía oscura / Luminaria",
            isOfficialBulletin = false,
            distanceMeters = 1200,
            isUserContribution = false
        ),
        ReportEntity(
            id = "rep-003",
            title = "Operativo de control activo en corredor Av. San Juan",
            description = "Comando Central La Candelaria en verificación de antecedentes y requisa de vehículos en marcha. Corredor seguro y despejado.",
            comunaId = 10,
            comunaName = "Boletín Oficial • MEVAL Valle de Aburrá",
            address = "Av. San Juan con Carabobo",
            securityLevel = SecurityLevel.LEVEL_2,
            relativeTime = "Hace 1 hora",
            validatedByNeighbors = 32,
            policeQuadrant = "MEVAL Central",
            usefulVotes = 52,
            falseAlarmVotes = 0,
            hasWitnessPhoto = false,
            photoUrl = null,
            category = "Operativo policial",
            isOfficialBulletin = true,
            officialEntityName = "Comando Central La Candelaria",
            distanceMeters = 400,
            isUserContribution = false
        ),
        ReportEntity(
            id = "rep-004",
            title = "Presencia sospechosa y marcación de locales",
            description = "Transeúntes y tenderos reportan dos personas tomando fotografías fijas a las cerraduras de negocios comerciales en horas de la tarde.",
            comunaId = 11,
            comunaName = "Laureles • Comuna 11",
            address = "Transversal 39B con Circular 4",
            securityLevel = SecurityLevel.LEVEL_4,
            relativeTime = "Hace 2 horas",
            validatedByNeighbors = 9,
            policeQuadrant = "Cuadrante 11-1",
            usefulVotes = 15,
            falseAlarmVotes = 2,
            hasWitnessPhoto = true,
            photoUrl = null,
            category = "Presencia sospechosa",
            distanceMeters = 850,
            isUserContribution = true
        )
    )

    private val reportsFlow = MutableStateFlow(initialReports)

    override fun getReports(tabFilter: CommunityTabFilter, radius: DistanceRadiusFilter): Flow<List<ReportEntity>> {
        return reportsFlow.map { list ->
            list.filter { report ->
                val tabMatches = when (tabFilter) {
                    CommunityTabFilter.REPORTS -> !report.isOfficialBulletin
                    CommunityTabFilter.POLICE_ALERTS -> report.isOfficialBulletin
                    CommunityTabFilter.MY_CONTRIBUTIONS -> report.isUserContribution
                }
                val radiusMatches = report.distanceMeters <= radius.maxMeters
                tabMatches && radiusMatches
            }
        }
    }

    override fun voteUseful(reportId: String) {
        reportsFlow.value = reportsFlow.value.map { report ->
            if (report.id == reportId && !report.userVotedUseful) {
                report.copy(
                    usefulVotes = report.usefulVotes + 1,
                    userVotedUseful = true
                )
            } else report
        }
    }

    override fun voteFalseAlarm(reportId: String) {
        reportsFlow.value = reportsFlow.value.map { report ->
            if (report.id == reportId && !report.userVotedFalseAlarm) {
                report.copy(
                    falseAlarmVotes = report.falseAlarmVotes + 1,
                    userVotedFalseAlarm = true
                )
            } else report
        }
    }

    override fun submitReport(
        title: String,
        description: String,
        comunaId: Int,
        comunaName: String,
        securityLevel: SecurityLevel,
        category: String,
        hasWitnessPhoto: Boolean
    ) {
        val newReport = ReportEntity(
            id = "rep-user-${System.currentTimeMillis()}",
            title = title,
            description = description,
            comunaId = comunaId,
            comunaName = comunaName,
            address = "Sector $comunaName",
            securityLevel = securityLevel,
            relativeTime = "Justo ahora",
            validatedByNeighbors = 1,
            policeQuadrant = "En validación CAD 123",
            usefulVotes = 1,
            falseAlarmVotes = 0,
            hasWitnessPhoto = hasWitnessPhoto,
            photoUrl = null,
            category = category,
            distanceMeters = 300,
            isUserContribution = true
        )
        reportsFlow.value = listOf(newReport) + reportsFlow.value
    }
}
