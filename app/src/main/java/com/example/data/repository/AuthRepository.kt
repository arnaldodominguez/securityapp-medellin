package com.example.data.repository

import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthRepository {
    fun getUserProfile(): Flow<UserProfile>
    fun registerUser(
        fullName: String,
        documentType: String,
        documentNumber: String,
        email: String,
        comunaId: Int,
        sosName: String,
        sosPhone: String
    ): Boolean
    fun loginUser(email: String): Boolean
    fun updatePreferences(
        notifyTerritoryCross: Boolean,
        minSensitivityLevel: Int,
        nightSilentMode: Boolean
    )
    fun updateSosContact(name: String, phone: String)
}

class AuthRepositoryImpl : AuthRepository {
    private val profileFlow = MutableStateFlow(UserProfile())

    override fun getUserProfile(): Flow<UserProfile> = profileFlow.asStateFlow()

    override fun registerUser(
        fullName: String,
        documentType: String,
        documentNumber: String,
        email: String,
        comunaId: Int,
        sosName: String,
        sosPhone: String
    ): Boolean {
        val isInstitutional = email.endsWith("@unal.edu.co") ||
                email.endsWith("@udea.edu.co") ||
                email.endsWith("@eafit.edu.co")

        profileFlow.value = profileFlow.value.copy(
            fullName = fullName,
            documentType = documentType,
            documentNumber = documentNumber,
            email = email,
            isInstitutionalEmail = isInstitutional,
            residenceComunaId = comunaId,
            sosContactName = sosName.ifBlank { "Contacto SOS Principal" },
            sosContactPhone = sosPhone.ifBlank { "+57 300 123 4567" },
            isLoggedIn = true
        )
        return true
    }

    override fun loginUser(email: String): Boolean {
        val isInstitutional = email.endsWith("@unal.edu.co") ||
                email.endsWith("@udea.edu.co") ||
                email.endsWith("@eafit.edu.co")

        profileFlow.value = profileFlow.value.copy(
            email = email,
            isInstitutionalEmail = isInstitutional,
            isLoggedIn = true
        )
        return true
    }

    override fun updatePreferences(
        notifyTerritoryCross: Boolean,
        minSensitivityLevel: Int,
        nightSilentMode: Boolean
    ) {
        profileFlow.value = profileFlow.value.copy(
            notifyTerritoryCross = notifyTerritoryCross,
            minAlertSensitivityLevel = minSensitivityLevel,
            nightSilentMode = nightSilentMode
        )
    }

    override fun updateSosContact(name: String, phone: String) {
        profileFlow.value = profileFlow.value.copy(
            sosContactName = name,
            sosContactPhone = phone
        )
    }
}
