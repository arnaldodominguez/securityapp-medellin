package com.example.domain.usecase

import com.example.data.model.ComunaZone
import com.example.data.model.SecurityLevel

/**
 * Caso de Uso: Evaluar Riesgo Territorial
 * Implementa la lógica matemática de la matriz tripartita de Medellín:
 * Vector 1: Registros Ciudadanos Validados (PoT)
 * Vector 2: Datos Abiertos SISC / Policía 123
 * Vector 3: Detección Espaciotemporal Kernel Density Estimation (KDE)
 */
class EvaluateZoneRiskUseCase {

    operator fun invoke(comuna: ComunaZone): SecurityLevel {
        val score = comuna.riskScore
        return SecurityLevel.fromRiskScore(score)
    }

    fun calculateTripartiteScore(
        citizenReportsCount: Int,
        siscIncidentsCount: Int,
        kdeDensityFactor: Float // 0.0 to 1.0
    ): Int {
        val weightCitizen = 0.30f
        val weightSisc = 0.45f
        val weightKde = 0.25f

        val normalizedCitizen = (citizenReportsCount.coerceIn(0, 30) / 30f) * 100f
        val normalizedSisc = (siscIncidentsCount.coerceIn(0, 100) / 100f) * 100f
        val normalizedKde = (kdeDensityFactor.coerceIn(0f, 1f)) * 100f

        val totalRisk = (normalizedCitizen * weightCitizen) +
                (normalizedSisc * weightSisc) +
                (normalizedKde * weightKde)

        return totalRisk.toInt().coerceIn(0, 100)
    }
}
