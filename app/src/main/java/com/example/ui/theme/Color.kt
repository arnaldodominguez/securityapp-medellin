package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Figma Community & Urban Design Tokens
val FigmaTealPrimary = Color(0xFF406D67)
val FigmaTealDark = Color(0xFF2C544E)
val FigmaTealLight = Color(0xFFEDF5F4)
val FigmaTealChip = Color(0xFFEBF2F1)
val FigmaTextPrimary = Color(0xFF162E2B)
val FigmaTextSecondary = Color(0xFF5B7B76)
val FigmaTextMuted = Color(0xFF829E9A)
val FigmaBorder = Color(0xFFE2ECEB)
val FigmaBackground = Color(0xFFFAFCFC)
val FigmaSurface = Color(0xFFFFFFFF)
val FigmaInputBg = Color(0xFFF1F5F5)
val FigmaAccentGreen = Color(0xFF4E8D7C)
val FigmaAccentBlue = Color(0xFF4A6B82)
val FigmaPinDark = Color(0xFF1E2928)

// Tactical Legacy Tokens (aliased to Figma palette for full consistency)
val TacticalBackground = FigmaBackground
val TacticalSurface = FigmaSurface
val TacticalSurfaceLow = Color(0xFFF6FAF9)
val TacticalSurfaceCard = FigmaSurface
val TacticalSurfaceHigh = Color(0xFFF1F5F5)
val TacticalSurfaceHighest = FigmaTealChip
val TacticalSurfaceBright = Color(0xFFFFFFFF)

// Primary Civic & Brand Colors
val TacticalPrimary = FigmaTealPrimary
val TacticalPrimaryContainer = FigmaTealPrimary
val TacticalOnPrimary = Color(0xFFFFFFFF)
val TacticalOnPrimaryContainer = Color(0xFFFFFFFF)

// Secondary Telemetry & Active Status
val TacticalSecondary = Color(0xFF4E8D7C)
val TacticalSecondaryContainer = FigmaTealLight
val TacticalOnSecondary = Color(0xFF162E2B)

// Tertiary Emergency & SOS Accents
val TacticalTertiary = Color(0xFFFFB3AD)
val TacticalTertiaryContainer = Color(0xFFEF4444)
val TacticalOnTertiary = Color(0xFFFFFFFF)
val TacticalSosRed = Color(0xFFDC2626)

// On-Surface & Outlines
val TacticalOnSurface = FigmaTextPrimary
val TacticalOnSurfaceVariant = FigmaTextSecondary
val TacticalOutline = FigmaBorder
val TacticalOutlineVariant = Color(0xFFD3E0DE)

// 7-Level Security Semaphore Official Colors (Núcleo de Dominio)
val SemaphoreLevel1 = Color(0xFF10B981) // Nivel 1 - Óptimo (0 - 14%)
val SemaphoreLevel2 = Color(0xFF84CC16) // Nivel 2 - Seguro (15 - 28%)
val SemaphoreLevel3 = Color(0xFFEAB308) // Nivel 3 - Leve (29 - 42%)
val SemaphoreLevel4 = Color(0xFFF59E0B) // Nivel 4 - Atención (43 - 56%)
val SemaphoreLevel5 = Color(0xFFF97316) // Nivel 5 - Moderado (57 - 70%)
val SemaphoreLevel6 = Color(0xFFEF4444) // Nivel 6 - Alto Riesgo (71 - 85%)
val SemaphoreLevel7 = Color(0xFF991B1B) // Nivel 7 - Crítico (86 - 100%)
