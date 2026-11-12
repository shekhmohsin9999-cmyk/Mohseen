package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PaymentAlertRepository
import com.example.data.UiNotice
import com.example.model.LicenseStatus
import com.example.model.MerchantProfile
import com.example.model.NotificationSupportSettings
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.model.VoiceSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PaymentAlertRepository

    init {
        repo.initialize(application.applicationContext)
    }

    val transactions: StateFlow<List<PaymentTransaction>> = repo.transactions
    val voiceSettings: StateFlow<VoiceSettings> = repo.voiceSettings
    val notificationSettings: StateFlow<NotificationSupportSettings> = repo.notificationSettings
    val licenseStatus: StateFlow<LicenseStatus> = repo.licenseStatus
    val merchantProfile: StateFlow<MerchantProfile> = repo.merchantProfile
    val isServiceActive: StateFlow<Boolean> = repo.isServiceActive
    val isSpeaking: StateFlow<Boolean> = repo.isSpeaking
    val currentSpokenText: StateFlow<String> = repo.currentSpokenText
    val uiNotice: SharedFlow<UiNotice> = repo.uiNotice

    // Navigation Tab (0: Dashboard, 1: History, 2: Voice, 3: Notifications, 4: License)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    // Modal dialog controls for app-wide access
    private val _showSetupInstructions = MutableStateFlow(false)
    val showSetupInstructions: StateFlow<Boolean> = _showSetupInstructions.asStateFlow()

    fun openSetupInstructions() {
        _showSetupInstructions.value = true
    }

    fun closeSetupInstructions() {
        _showSetupInstructions.value = false
    }

    private val _showCustomVoiceModal = MutableStateFlow(false)
    val showCustomVoiceModal: StateFlow<Boolean> = _showCustomVoiceModal.asStateFlow()

    fun openCustomVoiceModal() {
        _showCustomVoiceModal.value = true
    }

    fun closeCustomVoiceModal() {
        _showCustomVoiceModal.value = false
    }

    // License activation loading state
    private val _isActivatingLicense = MutableStateFlow(false)
    val isActivatingLicense: StateFlow<Boolean> = _isActivatingLicense.asStateFlow()

    // History Filters
    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _historyAppFilter = MutableStateFlow<UpiApp?>(null)
    val historyAppFilter: StateFlow<UpiApp?> = _historyAppFilter.asStateFlow()

    private val _historyDateFilter = MutableStateFlow("Today")
    val historyDateFilter: StateFlow<String> = _historyDateFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun setAppFilter(app: UpiApp?) {
        _historyAppFilter.value = app
    }

    fun setDateFilter(filter: String) {
        _historyDateFilter.value = filter
    }

    fun navigateToHistoryWithFilter(app: UpiApp?) {
        _historyAppFilter.value = app
        _currentTab.value = 1
    }

    // Filtered transactions for History screen
    val filteredTransactions: StateFlow<List<PaymentTransaction>> = combine(
        transactions,
        _historySearchQuery,
        _historyAppFilter,
        _historyDateFilter
    ) { list, query, appFilter, dateFilter ->
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = calendar.timeInMillis
        val startOfYesterday = startOfToday - 86400000L
        val startOfLast7Days = startOfToday - (7 * 86400000L)

        list.filter { txn ->
            val matchesQuery = query.isBlank() ||
                    txn.customerName.contains(query, ignoreCase = true) ||
                    txn.upiRefId.contains(query, ignoreCase = true) ||
                    txn.amount.toString().contains(query)

            val matchesApp = appFilter == null || txn.upiApp == appFilter

            val matchesDate = when (dateFilter) {
                "Today" -> txn.timestamp >= startOfToday
                "Yesterday" -> txn.timestamp in startOfYesterday until startOfToday
                "Last 7 Days" -> txn.timestamp >= startOfLast7Days
                else -> true
            }

            matchesQuery && matchesApp && matchesDate
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metrics for Dashboard
    val dashboardMetrics = transactions.combine(transactions) { _, _ ->
        repo.getTodayMetrics()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0.0, 0, emptyMap<UpiApp, Double>()))

    // Actions
    fun triggerDemoPayment(amount: Double, customerName: String, upiApp: UpiApp) {
        repo.triggerDemoPayment(amount, customerName, upiApp, speakImmediately = true)
    }

    fun repeatPayment(transaction: PaymentTransaction) {
        repo.repeatAnnouncement(transaction)
    }

    fun stopSpeaking() {
        repo.stopSpeaking()
    }

    fun playChime() {
        repo.playSoundboxChime()
    }

    fun testVoiceAnnouncement() {
        val sampleTxn = PaymentTransaction(
            id = "SAMPLE-TEST",
            customerName = "Rahul Sharma",
            amount = 500.0,
            upiApp = UpiApp.PHONEPE,
            timestamp = System.currentTimeMillis(),
            upiRefId = "UPI/998127391/CR",
            isDemo = true
        )
        repo.repeatAnnouncement(sampleTxn)
    }

    fun updateVoiceSettings(newSettings: VoiceSettings) {
        repo.updateVoiceSettings(getApplication(), newSettings)
    }

    fun updateNotificationSettings(newSettings: NotificationSupportSettings) {
        repo.updateNotificationSettings(newSettings)
    }

    fun updateMerchantProfile(newProfile: MerchantProfile) {
        repo.updateMerchantProfile(newProfile)
    }

    fun activateLicense(key: String, onFinished: ((Result<String>) -> Unit)? = null) {
        viewModelScope.launch {
            _isActivatingLicense.value = true
            val result = repo.activateLicense(getApplication(), key)
            _isActivatingLicense.value = false
            onFinished?.invoke(result)
        }
    }

    fun resetLicenseToTrial() {
        repo.resetLicenseToTrial(getApplication())
    }

    fun toggleDemoMode() {
        repo.toggleDemoMode(getApplication())
    }

    fun toggleServiceSimulation() {
        repo.toggleServiceSimulation()
    }
}
