package com.example.data

import android.content.Context
import com.example.model.LicenseStatus
import com.example.model.MerchantProfile
import com.example.model.NotificationSupportSettings
import com.example.model.PaymentStatus
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.model.VoiceLanguage
import com.example.model.VoiceSettings
import com.example.tts.TtsAnnouncementEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

sealed class UiNotice {
    data class Success(val message: String) : UiNotice()
    data class Error(val message: String) : UiNotice()
    data class Info(val message: String) : UiNotice()
}

object PaymentAlertRepository {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var ttsEngine: TtsAnnouncementEngine? = null

    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(emptyList())
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    private val _voiceSettings = MutableStateFlow(VoiceSettings())
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    private val _notificationSettings = MutableStateFlow(NotificationSupportSettings())
    val notificationSettings: StateFlow<NotificationSupportSettings> = _notificationSettings.asStateFlow()

    private val _licenseStatus = MutableStateFlow(LicenseStatus())
    val licenseStatus: StateFlow<LicenseStatus> = _licenseStatus.asStateFlow()

    private val _merchantProfile = MutableStateFlow(MerchantProfile())
    val merchantProfile: StateFlow<MerchantProfile> = _merchantProfile.asStateFlow()

    private val _isServiceActive = MutableStateFlow(true) // Initialized active for soundbox demo
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSpokenText = MutableStateFlow("")
    val currentSpokenText: StateFlow<String> = _currentSpokenText.asStateFlow()

    private val _uiNotice = MutableSharedFlow<UiNotice>(extraBufferCapacity = 1)
    val uiNotice: SharedFlow<UiNotice> = _uiNotice.asSharedFlow()

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        isInitialized = true

        ttsEngine = TtsAnnouncementEngine(context.applicationContext).apply {
            onSpeechStateChanged = { speaking, text ->
                _isSpeaking.value = speaking
                _currentSpokenText.value = text
            }
        }

        loadPreferences(context)
        if (_transactions.value.isEmpty()) {
            loadInitialDemoTransactions()
        }
    }

    private fun loadPreferences(context: Context) {
        val prefs = context.getSharedPreferences("payalert_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("voice_lang", VoiceLanguage.HINDI.name) ?: VoiceLanguage.HINDI.name
        val speechRate = prefs.getFloat("speech_rate", 1.0f)
        val pitch = prefs.getFloat("pitch", 1.0f)
        val volumeBoost = prefs.getBoolean("volume_boost", true)
        val playChime = prefs.getBoolean("play_chime", true)
        val isActivated = prefs.getBoolean("license_activated", true)
        val isDemo = prefs.getBoolean("license_demo_mode", true)

        val lang = try {
            VoiceLanguage.valueOf(langCode)
        } catch (e: Exception) {
            VoiceLanguage.HINDI
        }

        _voiceSettings.value = VoiceSettings(
            language = lang,
            speechRate = speechRate,
            pitch = pitch,
            volumeBoost = volumeBoost,
            playChimeBefore = playChime
        )

        _licenseStatus.value = _licenseStatus.value.copy(
            isActivated = isActivated,
            isDemoMode = isDemo
        )
    }

    fun savePreferences(context: Context) {
        val prefs = context.getSharedPreferences("payalert_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("voice_lang", _voiceSettings.value.language.name)
            putFloat("speech_rate", _voiceSettings.value.speechRate)
            putFloat("pitch", _voiceSettings.value.pitch)
            putBoolean("volume_boost", _voiceSettings.value.volumeBoost)
            putBoolean("play_chime", _voiceSettings.value.playChimeBefore)
            putBoolean("license_activated", _licenseStatus.value.isActivated)
            putBoolean("license_demo_mode", _licenseStatus.value.isDemoMode)
            apply()
        }
    }

    private fun loadInitialDemoTransactions() {
        val now = System.currentTimeMillis()
        val minute = 60 * 1000L
        val hour = 60 * minute

        val demoList = listOf(
            PaymentTransaction(
                id = "TXN-882194",
                customerName = "Rahul Sharma",
                amount = 500.0,
                upiApp = UpiApp.PHONEPE,
                timestamp = now - 4 * minute,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412891002348/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882193",
                customerName = "Pooja Patel",
                amount = 1250.0,
                upiApp = UpiApp.GPAY,
                timestamp = now - 22 * minute,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412890981122/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882192",
                customerName = "Amit Kumar",
                amount = 150.0,
                upiApp = UpiApp.PAYTM,
                timestamp = now - 48 * minute,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412890451990/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882191",
                customerName = "Sneha Verma",
                amount = 2800.0,
                upiApp = UpiApp.PHONEPE,
                timestamp = now - 2 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412889123001/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882190",
                customerName = "Rajesh Gupta",
                amount = 80.0,
                upiApp = UpiApp.GPAY,
                timestamp = now - 3 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412888201948/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882189",
                customerName = "Vikram Singh",
                amount = 450.0,
                upiApp = UpiApp.PAYTM,
                timestamp = now - 4 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412887102938/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882188",
                customerName = "Priya Nair",
                amount = 2100.0,
                upiApp = UpiApp.PHONEPE,
                timestamp = now - 5 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412886491028/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882187",
                customerName = "Ankit Jain",
                amount = 320.0,
                upiApp = UpiApp.GPAY,
                timestamp = now - 6 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412885901239/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882186",
                customerName = "Deepak Rao",
                amount = 750.0,
                upiApp = UpiApp.PAYTM,
                timestamp = now - 7 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412884102948/CR",
                isDemo = true
            ),
            PaymentTransaction(
                id = "TXN-882185",
                customerName = "Sunita Devi",
                amount = 120.0,
                upiApp = UpiApp.BHIM,
                timestamp = now - 8 * hour,
                status = PaymentStatus.SUCCESS,
                upiRefId = "UPI/412883901928/CR",
                isDemo = true
            )
        )
        _transactions.value = demoList
    }

    fun updateServiceStatus(active: Boolean) {
        _isServiceActive.value = active
    }

    fun toggleServiceSimulation() {
        val current = _isServiceActive.value
        _isServiceActive.value = !current
        val msg = if (!current) "Notification listener simulated CONNECTED" else "Notification listener simulated DISCONNECTED"
        _uiNotice.tryEmit(UiNotice.Info(msg))
    }

    fun recordIncomingPayment(context: Context, transaction: PaymentTransaction) {
        val notifSettings = _notificationSettings.value

        val isEnabled = when (transaction.upiApp) {
            UpiApp.GPAY -> notifSettings.gpayEnabled
            UpiApp.PHONEPE -> notifSettings.phonepeEnabled
            UpiApp.PAYTM -> notifSettings.paytmEnabled
            UpiApp.BHIM -> notifSettings.bhimEnabled
            UpiApp.OTHER -> true
        }

        if (!isEnabled) return
        if (transaction.amount < notifSettings.minAmountThreshold) return

        _transactions.value = listOf(transaction) + _transactions.value
        ttsEngine?.announcePayment(transaction, _voiceSettings.value)
    }

    fun triggerDemoPayment(
        amount: Double,
        customerName: String,
        upiApp: UpiApp,
        speakImmediately: Boolean = true
    ): PaymentTransaction {
        val newTxn = PaymentTransaction(
            id = "DEMO-${UUID.randomUUID().toString().take(6).uppercase()}",
            customerName = customerName.ifBlank { "Customer" },
            amount = amount,
            upiApp = upiApp,
            timestamp = System.currentTimeMillis(),
            status = PaymentStatus.SUCCESS,
            upiRefId = "UPI/${System.currentTimeMillis().toString().takeLast(10)}/CR",
            isDemo = true
        )
        _transactions.value = listOf(newTxn) + _transactions.value

        if (speakImmediately) {
            ttsEngine?.announcePayment(newTxn, _voiceSettings.value)
        }
        _uiNotice.tryEmit(UiNotice.Success("Payment received: ₹${amount.toInt()} from $customerName via ${upiApp.displayName}"))
        return newTxn
    }

    fun announceCustomText(text: String) {
        ttsEngine?.speakText(text, _voiceSettings.value)
    }

    fun playSoundboxChime() {
        ttsEngine?.playSoundboxChime()
        _uiNotice.tryEmit(UiNotice.Info("Soundbox chime played"))
    }

    fun repeatAnnouncement(transaction: PaymentTransaction) {
        ttsEngine?.announcePayment(transaction, _voiceSettings.value)
        _uiNotice.tryEmit(UiNotice.Info("Announcing ₹${transaction.amount.toInt()} from ${transaction.customerName}"))
    }

    fun stopSpeaking() {
        ttsEngine?.stop()
        _uiNotice.tryEmit(UiNotice.Info("Speech announcement stopped"))
    }

    fun updateVoiceSettings(context: Context, newSettings: VoiceSettings) {
        _voiceSettings.value = newSettings
        savePreferences(context)
        _uiNotice.tryEmit(UiNotice.Success("Voice set to ${newSettings.language.displayName} (${newSettings.speechRate}x)"))
    }

    fun updateNotificationSettings(newSettings: NotificationSupportSettings) {
        _notificationSettings.value = newSettings
        _uiNotice.tryEmit(UiNotice.Success("UPI notification settings updated"))
    }

    fun updateMerchantProfile(newProfile: MerchantProfile) {
        _merchantProfile.value = newProfile
        _uiNotice.tryEmit(UiNotice.Success("Merchant profile saved: ${newProfile.shopName}"))
    }

    /**
     * Requirement: License activation must validate a demo license only in demo mode.
     */
    suspend fun activateLicense(context: Context, key: String): Result<String> {
        delay(500) // Realistic activation verification delay
        val cleanKey = key.trim().uppercase()
        val isDemo = _licenseStatus.value.isDemoMode

        if (isDemo) {
            val isDemoKeyValid = cleanKey == "PAYALERT100" ||
                    cleanKey == "PA-DEMO-2026" ||
                    cleanKey == "PA-TRIAL-100" ||
                    cleanKey.startsWith("PA-DEMO") ||
                    (cleanKey.startsWith("PA-") && cleanKey.length >= 10)

            return if (isDemoKeyValid) {
                _licenseStatus.value = _licenseStatus.value.copy(
                    isActivated = true,
                    licenseKey = cleanKey,
                    activatedDate = System.currentTimeMillis(),
                    lastValidationMessage = "Demo License Activated Successfully"
                )
                savePreferences(context)
                _uiNotice.tryEmit(UiNotice.Success("₹100 Demo License Activated Successfully!"))
                Result.success("Demo License Activated Successfully!")
            } else {
                val errorMsg = "Invalid Demo Key: '$cleanKey'. In Demo Mode, use PAYALERT100 or PA-DEMO-2026."
                _uiNotice.tryEmit(UiNotice.Error(errorMsg))
                Result.failure(IllegalArgumentException(errorMsg))
            }
        } else {
            // Live production mode
            val isProdKeyValid = cleanKey.startsWith("PA-LIVE-") && cleanKey.length == 16
            return if (isProdKeyValid) {
                _licenseStatus.value = _licenseStatus.value.copy(
                    isActivated = true,
                    licenseKey = cleanKey,
                    activatedDate = System.currentTimeMillis(),
                    lastValidationMessage = "Live Merchant License Active"
                )
                savePreferences(context)
                _uiNotice.tryEmit(UiNotice.Success("Live License Activated!"))
                Result.success("Live License Activated!")
            } else {
                val errorMsg = "Production key verification requires backend server. Switch to Demo Mode to test with PAYALERT100."
                _uiNotice.tryEmit(UiNotice.Error(errorMsg))
                Result.failure(IllegalArgumentException(errorMsg))
            }
        }
    }

    fun resetLicenseToTrial(context: Context) {
        _licenseStatus.value = _licenseStatus.value.copy(
            isActivated = false,
            licenseKey = "TRIAL-MODE",
            trialDaysRemaining = 14
        )
        savePreferences(context)
        _uiNotice.tryEmit(UiNotice.Info("License reset to 14-day Trial Mode"))
    }

    fun toggleDemoMode(context: Context) {
        val currentDemo = _licenseStatus.value.isDemoMode
        _licenseStatus.value = _licenseStatus.value.copy(isDemoMode = !currentDemo)
        savePreferences(context)
        val mode = if (!currentDemo) "Demo Mode Enabled" else "Live Mode Enabled"
        _uiNotice.tryEmit(UiNotice.Info(mode))
    }

    fun getTodayMetrics(): Triple<Double, Int, Map<UpiApp, Double>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        val todayList = _transactions.value.filter { it.timestamp >= startOfDay && it.status == PaymentStatus.SUCCESS }
        val totalAmount = todayList.sumOf { it.amount }
        val count = todayList.size

        val breakdown = todayList.groupBy { it.upiApp }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        return Triple(totalAmount, count, breakdown)
    }
}
