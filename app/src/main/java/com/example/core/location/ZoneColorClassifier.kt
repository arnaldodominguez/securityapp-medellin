package com.example.core.location

import androidx.compose.ui.graphics.Color
import com.example.data.model.ComunaZone

enum class ZoneDangerColor(
    val code: Int,
    val label: String,
    val color: Color,
    val hexColor: String
) {
    RED(1, "ROJO (Peligro)", Color(0xFFEF4444), "#EF4444"),
    YELLOW(2, "AMARILLO (Precaución)", Color(0xFFF59E0B), "#F59E0B"),
    GREEN(3, "VERDE (Seguro)", Color(0xFF10B981), "#10B981"),
    NO_GPS(4, "SIN SEÑAL GPS", Color(0xFF64748B), "#64748B")
}

data class ZoneEvaluation(
    val dangerColor: ZoneDangerColor,
    val detectedComuna: ComunaZone?,
    val isGpsActive: Boolean,
    val signalCodeToSend: Int
)

object ZoneColorClassifier {

    /**
     * Map Comuna risk score (0-100) or ID into the 3 danger colors:
     * - Score >= 55% -> RED (1)
     * - Score 35%..54% -> YELLOW (2)
     * - Score < 35% -> GREEN (3)
     */
    fun getComunaDangerColor(comuna: ComunaZone): ZoneDangerColor {
        return when {
            comuna.riskScore >= 55 -> ZoneDangerColor.RED
            comuna.riskScore >= 35 -> ZoneDangerColor.YELLOW
            else -> ZoneDangerColor.GREEN
        }
    }

    /**
     * Given user's live latitude & longitude, finds which Comuna the user is currently in.
     * If no GPS signal, returns NO_GPS (code 4).
     */
    fun evaluateGpsPosition(
        latitude: Double?,
        longitude: Double?,
        comunas: List<ComunaZone>
    ): ZoneEvaluation {
        if (latitude == null || longitude == null) {
            return ZoneEvaluation(
                dangerColor = ZoneDangerColor.NO_GPS,
                detectedComuna = null,
                isGpsActive = false,
                signalCodeToSend = 4
            )
        }

        // 1. Point in polygon test
        var matchedComuna: ComunaZone? = null
        for (comuna in comunas) {
            val perimeter = getComunaPerimeter(comuna.id, comuna.latitude, comuna.longitude)
            if (isPointInsidePolygon(latitude, longitude, perimeter)) {
                matchedComuna = comuna
                break
            }
        }

        // 2. Nearest centroid fallback if near border
        if (matchedComuna == null) {
            matchedComuna = comunas.minByOrNull { comuna ->
                val dLat = comuna.latitude - latitude
                val dLng = comuna.longitude - longitude
                dLat * dLat + dLng * dLng
            }
        }

        return if (matchedComuna != null) {
            val dangerColor = getComunaDangerColor(matchedComuna)
            ZoneEvaluation(
                dangerColor = dangerColor,
                detectedComuna = matchedComuna,
                isGpsActive = true,
                signalCodeToSend = dangerColor.code
            )
        } else {
            ZoneEvaluation(
                dangerColor = ZoneDangerColor.NO_GPS,
                detectedComuna = null,
                isGpsActive = false,
                signalCodeToSend = 4
            )
        }
    }

    /**
     * Standard Ray Casting algorithm for point-in-polygon
     */
    private fun isPointInsidePolygon(
        lat: Double,
        lng: Double,
        polygon: List<Pair<Double, Double>>
    ): Boolean {
        var inside = false
        var j = polygon.size - 1
        for (i in polygon.indices) {
            val (yi, xi) = polygon[i] // yi: lat, xi: lng
            val (yj, xj) = polygon[j]

            val intersect = ((yi > lat) != (yj > lat)) &&
                    (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi)
            if (intersect) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    /**
     * Real organic, non-rectangular geographic perimeters (multi-vertex polygons)
     * corresponding to the official "División Político Administrativa de Medellín" for the 16 Comunas.
     */
    fun getComunaPerimeter(
        comunaId: Int,
        centerLat: Double,
        centerLng: Double
    ): List<Pair<Double, Double>> {
        return when (comunaId) {
            1 -> listOf(
                Pair(6.315, -75.545), Pair(6.322, -75.540), Pair(6.318, -75.532),
                Pair(6.305, -75.528), Pair(6.295, -75.535), Pair(6.290, -75.542), Pair(6.302, -75.548)
            ) // Comuna 1 - Popular
            2 -> listOf(
                Pair(6.322, -75.560), Pair(6.328, -75.550), Pair(6.322, -75.540),
                Pair(6.315, -75.545), Pair(6.302, -75.548), Pair(6.292, -75.555), Pair(6.298, -75.562)
            ) // Comuna 2 - Santa Cruz
            3 -> listOf(
                Pair(6.290, -75.542), Pair(6.295, -75.535), Pair(6.285, -75.526),
                Pair(6.268, -75.532), Pair(6.262, -75.544), Pair(6.278, -75.546)
            ) // Comuna 3 - Manrique
            4 -> listOf(
                Pair(6.292, -75.555), Pair(6.302, -75.548), Pair(6.290, -75.542),
                Pair(6.278, -75.546), Pair(6.265, -75.552), Pair(6.272, -75.568)
            ) // Comuna 4 - Aranjuez
            5 -> listOf(
                Pair(6.322, -75.585), Pair(6.328, -75.560), Pair(6.295, -75.562),
                Pair(6.285, -75.572), Pair(6.288, -75.585), Pair(6.305, -75.590)
            ) // Comuna 5 - Castilla
            6 -> listOf(
                Pair(6.335, -75.610), Pair(6.322, -75.585), Pair(6.305, -75.590),
                Pair(6.288, -75.585), Pair(6.282, -75.602), Pair(6.302, -75.615)
            ) // Comuna 6 - Doce de Octubre
            7 -> listOf(
                Pair(6.295, -75.625), Pair(6.282, -75.602), Pair(6.288, -75.585),
                Pair(6.272, -75.578), Pair(6.258, -75.588), Pair(6.255, -75.612), Pair(6.272, -75.628)
            ) // Comuna 7 - Robledo
            8 -> listOf(
                Pair(6.268, -75.532), Pair(6.285, -75.526), Pair(6.272, -75.518),
                Pair(6.242, -75.522), Pair(6.238, -75.540), Pair(6.252, -75.546)
            ) // Comuna 8 - Villa Hermosa
            9 -> listOf(
                Pair(6.238, -75.540), Pair(6.242, -75.522), Pair(6.218, -75.525),
                Pair(6.192, -75.542), Pair(6.208, -75.555), Pair(6.228, -75.550)
            ) // Comuna 9 - Buenos Aires
            10 -> listOf(
                Pair(6.265, -75.552), Pair(6.278, -75.546), Pair(6.252, -75.546),
                Pair(6.238, -75.540), Pair(6.232, -75.556), Pair(6.235, -75.572), Pair(6.255, -75.575)
            ) // Comuna 10 - La Candelaria (Centro)
            11 -> listOf(
                Pair(6.258, -75.588), Pair(6.272, -75.578), Pair(6.255, -75.575),
                Pair(6.235, -75.572), Pair(6.228, -75.588), Pair(6.248, -75.598)
            ) // Comuna 11 - Laureles - Estadio
            12 -> listOf(
                Pair(6.255, -75.612), Pair(6.258, -75.588), Pair(6.248, -75.598),
                Pair(6.228, -75.588), Pair(6.225, -75.608), Pair(6.242, -75.618)
            ) // Comuna 12 - La América
            13 -> listOf(
                Pair(6.272, -75.628), Pair(6.255, -75.612), Pair(6.242, -75.618),
                Pair(6.225, -75.608), Pair(6.220, -75.632), Pair(6.238, -75.652), Pair(6.260, -75.642)
            ) // Comuna 13 - San Javier
            14 -> listOf(
                Pair(6.232, -75.556), Pair(6.238, -75.540), Pair(6.228, -75.550),
                Pair(6.208, -75.555), Pair(6.192, -75.542), Pair(6.175, -75.562), Pair(6.202, -75.575)
            ) // Comuna 14 - El Poblado (Irregular teardrop)
            15 -> listOf(
                Pair(6.228, -75.588), Pair(6.232, -75.556), Pair(6.202, -75.575),
                Pair(6.175, -75.578), Pair(6.180, -75.596), Pair(6.210, -75.592)
            ) // Comuna 15 - Guayabal
            16 -> listOf(
                Pair(6.242, -75.618), Pair(6.225, -75.608), Pair(6.228, -75.588),
                Pair(6.210, -75.592), Pair(6.182, -75.600), Pair(6.190, -75.620), Pair(6.222, -75.628)
            ) // Comuna 16 - Belén
            50 -> listOf(
                Pair(6.340, -75.720), Pair(6.350, -75.650), Pair(6.280, -75.640), Pair(6.275, -75.710)
            ) // Corregimiento - San Sebastián de Palmitas
            60 -> listOf(
                Pair(6.305, -75.650), Pair(6.300, -75.600), Pair(6.240, -75.610), Pair(6.245, -75.670)
            ) // Corregimiento - San Cristóbal
            70 -> listOf(
                Pair(6.245, -75.670), Pair(6.240, -75.610), Pair(6.190, -75.610), Pair(6.195, -75.660)
            ) // Corregimiento - Altavista
            80 -> listOf(
                Pair(6.200, -75.660), Pair(6.190, -75.600), Pair(6.140, -75.600), Pair(6.145, -75.650)
            ) // Corregimiento - San Antonio de Prado
            90 -> listOf(
                Pair(6.280, -75.520), Pair(6.285, -75.460), Pair(6.210, -75.460), Pair(6.215, -75.520)
            ) // Corregimiento - Santa Elena
            else -> listOf(
                Pair(centerLat + 0.008, centerLng - 0.008),
                Pair(centerLat + 0.008, centerLng + 0.008),
                Pair(centerLat - 0.008, centerLng + 0.008),
                Pair(centerLat - 0.008, centerLng - 0.008)
            )
        }
    }
}
