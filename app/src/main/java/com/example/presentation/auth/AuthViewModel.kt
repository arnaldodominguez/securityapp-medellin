package com.example.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class AuthState(
    val profile: UserProfile = UserProfile(),
    val isSaving: Boolean = false,
    val feedbackMessage: String? = null,
    val testSosDispatched: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    init {
        authRepository.getUserProfile().onEach { p ->
            _uiState.value = _uiState.value.copy(profile = p)
        }.launchIn(viewModelScope)
    }

    fun updateProfile(
        fullName: String,
        documentType: String,
        documentNumber: String,
        email: String,
        comunaId: Int,
        sosName: String,
        sosPhone: String
    ) {
        authRepository.registerUser(
            fullName = fullName,
            documentType = documentType,
            documentNumber = documentNumber,
            email = email,
            comunaId = comunaId,
            sosName = sosName,
            sosPhone = sosPhone
        )
        _uiState.value = _uiState.value.copy(
            feedbackMessage = "¡Perfil ciudadano y contacto SOS actualizados exitosamente!"
        )
    }

    fun updatePreferences(
        notifyTerritoryCross: Boolean,
        minSensitivityLevel: Int,
        nightSilentMode: Boolean
    ) {
        authRepository.updatePreferences(
            notifyTerritoryCross = notifyTerritoryCross,
            minSensitivityLevel = minSensitivityLevel,
            nightSilentMode = nightSilentMode
        )
    }

    fun testSosDispatch() {
        _uiState.value = _uiState.value.copy(
            feedbackMessage = "Prueba SOS: SMS simulado transmitido a ${_uiState.value.profile.sosContactName} (${_uiState.value.profile.sosContactPhone})"
        )
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
