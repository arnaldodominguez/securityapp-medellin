package com.example.domain.usecase

import com.example.data.model.ComunaZone
import com.example.data.model.UserProfile

class TriggerSosUseCase {
    operator fun invoke(
        comuna: ComunaZone,
        userProfile: UserProfile
    ): SosDispatchResult {
        return SosDispatchResult(
            emergencyLine = "123",
            activeQuadrant = comuna.activeQuadrant,
            comunaName = comuna.name,
            riskLevel = comuna.securityLevel,
            timestamp = System.currentTimeMillis(),
            smsRecipientName = userProfile.sosContactName,
            smsRecipientPhone = userProfile.sosContactPhone,
            emergencyMessage = "ALERTA SOS: Me encuentro en ${comuna.name}, sector ${comuna.sectorName} (${comuna.latitude}, ${comuna.longitude}). Nivel de riesgo ${comuna.securityLevel.levelNumber} (${comuna.securityLevel.title}). Por favor comunícate con la Línea 123 o cuadrante ${comuna.activeQuadrant}."
        )
    }
}

data class SosDispatchResult(
    val emergencyLine: String,
    val activeQuadrant: String,
    val comunaName: String,
    val riskLevel: com.example.data.model.SecurityLevel,
    val timestamp: Long,
    val smsRecipientName: String,
    val smsRecipientPhone: String,
    val emergencyMessage: String
)
