package com.example.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.ComunaZone
import com.example.data.model.SecurityLevel

object NotificationHelper {

    fun generateGeofenceAlertTitle(comuna: ComunaZone): String {
        return "Cruce de Geocerca: ${comuna.name}"
    }

    fun generateGeofenceAlertBody(comuna: ComunaZone): String {
        val level = comuna.securityLevel
        return "${level.title} (Riesgo: ${comuna.riskScore}%). Cuadrante Policial: ${comuna.activeQuadrant}."
    }
}

object SmsHelper {
    fun sendEmergencySmsIntent(
        context: Context,
        phoneNumber: String,
        message: String
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to dialer
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
        }
    }

    fun openDialerIntent(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
