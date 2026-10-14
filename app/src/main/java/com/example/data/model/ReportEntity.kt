package com.example.data.model

data class ReportEntity(
    val id: String,
    val title: String,
    val description: String,
    val comunaId: Int,
    val comunaName: String,
    val address: String,
    val securityLevel: SecurityLevel,
    val relativeTime: String,
    val validatedByNeighbors: Int,
    val policeQuadrant: String? = null,
    val usefulVotes: Int,
    val falseAlarmVotes: Int,
    val hasWitnessPhoto: Boolean = false,
    val photoUrl: String? = null,
    val category: String,
    val isOfficialBulletin: Boolean = false,
    val officialEntityName: String? = null,
    val distanceMeters: Int = 450,
    val isUserContribution: Boolean = false,
    val userVotedUseful: Boolean = false,
    val userVotedFalseAlarm: Boolean = false
)

enum class CommunityTabFilter {
    REPORTS,
    POLICE_ALERTS,
    MY_CONTRIBUTIONS
}

enum class DistanceRadiusFilter(val label: String, val maxMeters: Int) {
    RADIUS_500M("500 m", 500),
    RADIUS_1KM("1 km", 1000),
    RADIUS_5KM("5 km", 5000),
    RADIUS_ALL("Todo Med", 50000)
}
