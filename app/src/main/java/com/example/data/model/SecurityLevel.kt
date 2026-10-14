package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SemaphoreLevel1
import com.example.ui.theme.SemaphoreLevel2
import com.example.ui.theme.SemaphoreLevel3
import com.example.ui.theme.SemaphoreLevel4
import com.example.ui.theme.SemaphoreLevel5
import com.example.ui.theme.SemaphoreLevel6
import com.example.ui.theme.SemaphoreLevel7

/**
 * Sistema Semafórico de 7 Niveles (Núcleo de Dominio)
 *
 * 1. NIVEL 1 - Óptimo (#10B981, Verde Esmeralda): 0 - 14% riesgo. Convivencia cívica alta.
 * 2. NIVEL 2 - Seguro (#84CC16, Verde Lima): 15 - 28% riesgo. Patrullaje regular.
 * 3. NIVEL 3 - Leve (#EAB308, Amarillo Dorado): 29 - 42% riesgo. Precaución preventiva diurna.
 * 4. NIVEL 4 - Atención (#F59E0B, Ámbar / Naranja Claro): 43 - 56% riesgo. Incidentes menores aislados.
 * 5. NIVEL 5 - Moderado (#F97316, Naranja Intenso): 57 - 70% riesgo. Precaución activa en corredores.
 * 6. NIVEL 6 - Alto Riesgo (#EF4444, Rojo Carmesí): 71 - 85% riesgo. Focos de hurto/conflicto reciente.
 * 7. NIVEL 7 - Crítico (#991B1B, Rojo Rubí Profundo): 86 - 100% riesgo. Despacho SOS y evitar tránsito.
 */
enum class SecurityLevel(
    val levelNumber: Int,
    val title: String,
    val hexColor: String,
    val color: Color,
    val minRisk: Int,
    val maxRisk: Int,
    val statusDescription: String,
    val recommendations: List<String>
) {
    LEVEL_1(
        levelNumber = 1,
        title = "NIVEL 1 • Óptimo",
        hexColor = "#10B981",
        color = SemaphoreLevel1,
        minRisk = 0,
        maxRisk = 14,
        statusDescription = "Convivencia cívica alta. Zona de flujo comunitario libre y seguro.",
        recommendations = listOf(
            "Transitar libremente en senderos peatonales y comerciales.",
            "Patrullajes comunitarios activos en horas diurnas y nocturnas.",
            "Mantener buenas prácticas de convivencia barrial."
        )
    ),
    LEVEL_2(
        levelNumber = 2,
        title = "NIVEL 2 • Seguro",
        hexColor = "#84CC16",
        color = SemaphoreLevel2,
        minRisk = 15,
        maxRisk = 28,
        statusDescription = "Patrullaje regular. Circulación general con bajo índice de incidentes.",
        recommendations = listOf(
            "Mantener vigilancia habitual de pertenencias en transporte público.",
            "Respetar las indicaciones de los cuadrantes policiales en patrullaje.",
            "Reportar cualquier luminaria apagada o vehículo en abandono."
        )
    ),
    LEVEL_3(
        levelNumber = 3,
        title = "NIVEL 3 • Leve",
        hexColor = "#EAB308",
        color = SemaphoreLevel3,
        minRisk = 29,
        maxRisk = 42,
        statusDescription = "Precaución preventiva diurna. Aglomeraciones y puntos ciegos detectados.",
        recommendations = listOf(
            "Evitar exhibir dispositivos móviles de alta gama en esquinas poco iluminadas.",
            "Preferir vías y avenidas principales con cámaras del 123.",
            "Verificar presencia de cuadrante antes de realizar transacciones en efectivo."
        )
    ),
    LEVEL_4(
        levelNumber = 4,
        title = "NIVEL 4 • Atención",
        hexColor = "#F59E0B",
        color = SemaphoreLevel4,
        minRisk = 43,
        maxRisk = 56,
        statusDescription = "Incidentes menores aislados. Frecuencia moderada de reportes en la última semana.",
        recommendations = listOf(
            "Activar monitoreo de geocerca en la app.",
            "Evitar callejones solitarios a partir de las 20:00 horas.",
            "Compartir ruta de viaje con un contacto de confianza si caminas solo."
        )
    ),
    LEVEL_5(
        levelNumber = 5,
        title = "NIVEL 5 • Moderado",
        hexColor = "#F97316",
        color = SemaphoreLevel5,
        minRisk = 57,
        maxRisk = 70,
        statusDescription = "Precaución activa en corredores. Incremento de hurtos a transeúntes y motos.",
        recommendations = listOf(
            "Evitar estacionar vehículos en vías secundarias sin vigilancia.",
            "Permanecer atento a motocicletas con parrillero en semáforos.",
            "En caso de anomalía, comunicarse de inmediato con el cuadrante activo."
        )
    ),
    LEVEL_6(
        levelNumber = 6,
        title = "NIVEL 6 • Alto Riesgo",
        hexColor = "#EF4444",
        color = SemaphoreLevel6,
        minRisk = 71,
        maxRisk = 85,
        statusDescription = "Focos de hurto y alteración del orden reciente. Alta probabilidad de incidentes.",
        recommendations = listOf(
            "No transitar a pie si no eres residente del sector.",
            "Utilizar transporte formal o caravanas de acompañamiento ciudadano.",
            "Tener listo el botón de llamada directa al 123 Medellín."
        )
    ),
    LEVEL_7(
        levelNumber = 7,
        title = "NIVEL 7 • Crítico",
        hexColor = "#991B1B",
        color = SemaphoreLevel7,
        minRisk = 86,
        maxRisk = 100,
        statusDescription = "Despacho SOS y evitar tránsito. Intervención operativa activa de fuerzas de seguridad.",
        recommendations = listOf(
            "EVITAR INGRESO O TRÁNSITO POR ESTA ZONA INMEDIATAMENTE.",
            "Si ya te encuentras adentro, resguárdate en un establecimiento seguro o CAI.",
            "El sistema transmitirá tu ubicación satelital a tu contacto de emergencia SOS si permaneces en el perímetro."
        )
    );

    companion object {
        fun fromRiskScore(score: Int): SecurityLevel {
            return entries.firstOrNull { score in it.minRisk..it.maxRisk } ?: LEVEL_4
        }

        fun fromLevelNumber(number: Int): SecurityLevel {
            return entries.firstOrNull { it.levelNumber == number } ?: LEVEL_1
        }
    }
}
