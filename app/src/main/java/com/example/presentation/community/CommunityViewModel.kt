package com.example.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CommunityTabFilter
import com.example.data.model.DistanceRadiusFilter
import com.example.data.model.ReportEntity
import com.example.data.model.SecurityLevel
import com.example.data.repository.ReportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class CommunityState(
    val reports: List<ReportEntity> = emptyList(),
    val activeTab: CommunityTabFilter = CommunityTabFilter.REPORTS,
    val activeRadius: DistanceRadiusFilter = DistanceRadiusFilter.RADIUS_1KM,
    val showCreateReportModal: Boolean = false,
    val processedTodayCount: Int = 87,
    val precisionIa: String = "94.2%",
    val successSnackbarMessage: String? = null
)

class CommunityViewModel(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityState())
    val uiState: StateFlow<CommunityState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        val tab = _uiState.value.activeTab
        val radius = _uiState.value.activeRadius
        reportsRepository.getReports(tab, radius).onEach { list ->
            _uiState.value = _uiState.value.copy(reports = list)
        }.launchIn(viewModelScope)
    }

    fun setTab(tab: CommunityTabFilter) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
        loadReports()
    }

    fun setRadius(radius: DistanceRadiusFilter) {
        _uiState.value = _uiState.value.copy(activeRadius = radius)
        loadReports()
    }

    fun voteUseful(reportId: String) {
        reportsRepository.voteUseful(reportId)
    }

    fun voteFalseAlarm(reportId: String) {
        reportsRepository.voteFalseAlarm(reportId)
    }

    fun openReportModal() {
        _uiState.value = _uiState.value.copy(showCreateReportModal = true)
    }

    fun closeReportModal() {
        _uiState.value = _uiState.value.copy(showCreateReportModal = false)
    }

    fun submitNewReport(
        title: String,
        description: String,
        comunaId: Int,
        comunaName: String,
        securityLevel: SecurityLevel,
        category: String,
        hasWitnessPhoto: Boolean
    ) {
        reportsRepository.submitReport(
            title = title,
            description = description,
            comunaId = comunaId,
            comunaName = comunaName,
            securityLevel = securityLevel,
            category = category,
            hasWitnessPhoto = hasWitnessPhoto
        )
        _uiState.value = _uiState.value.copy(
            showCreateReportModal = false,
            processedTodayCount = _uiState.value.processedTodayCount + 1,
            successSnackbarMessage = "¡Reporte transmitido a la Red CAD 123 y vecinos del sector!"
        )
        loadReports()
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(successSnackbarMessage = null)
    }
}
