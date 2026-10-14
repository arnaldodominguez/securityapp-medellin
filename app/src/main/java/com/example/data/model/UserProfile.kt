package com.example.data.model

data class UserProfile(
    val fullName: String = "Mariana Osorio Restrepo",
    val documentType: String = "CC",
    val documentNumber: String = "1037648291",
    val email: String = "mariana.osorio@unal.edu.co",
    val isInstitutionalEmail: Boolean = true,
    val residenceComunaId: Int = 11,
    val sosContactName: String = "Clara Osorio (Hermana)",
    val sosContactPhone: String = "+57 300 456 7890",
    val notifyTerritoryCross: Boolean = true,
    val minAlertSensitivityLevel: Int = 4,
    val nightSilentMode: Boolean = false,
    val isLoggedIn: Boolean = true
)
