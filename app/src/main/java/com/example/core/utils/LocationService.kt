package com.example.core.utils

import com.example.data.model.ComunaZone
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationService {

    /**
     * Calcula la distancia haversine en metros entre dos coordenadas geográficas
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0 // Radio de la Tierra en metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Encuentra la comuna más cercana dentro de Medellín según coordenadas GPS
     */
    fun findClosestComuna(
        currentLat: Double,
        currentLon: Double,
        comunas: List<ComunaZone>
    ): ComunaZone {
        return comunas.minByOrNull { comuna ->
            calculateDistanceMeters(currentLat, currentLon, comuna.latitude, comuna.longitude)
        } ?: comunas.first()
    }
}
