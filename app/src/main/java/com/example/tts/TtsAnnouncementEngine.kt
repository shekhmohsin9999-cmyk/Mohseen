package com.example.tts

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.model.VoiceLanguage
import com.example.model.VoiceSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TtsAnnouncementEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Main)
    private var activeSpeechJob: Job? = null

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    var onSpeechStateChanged: ((isSpeaking: Boolean, currentText: String) -> Unit)? = null
    var onAnnouncementStarted: ((text: String) -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TtsAnnouncementEngine", "TextToSpeech init error", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    scope.launch { onSpeechStateChanged?.invoke(true, utteranceId.orEmpty()) }
                }

                override fun onDone(utteranceId: String?) {
                    scope.launch { onSpeechStateChanged?.invoke(false, "") }
                }

                override fun onError(utteranceId: String?) {
                    scope.launch { onSpeechStateChanged?.invoke(false, "") }
                }
            })
            setLanguage(VoiceLanguage.HINDI)
            Log.i("TtsAnnouncementEngine", "TextToSpeech initialized successfully")
        } else {
            Log.w("TtsAnnouncementEngine", "TextToSpeech engine not ready (status: $status). Fallback mode active.")
            isTtsReady = false
        }
    }

    fun setLanguage(language: VoiceLanguage) {
        if (!isTtsReady || tts == null) return
        try {
            val result = tts?.setLanguage(language.locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                val fallback1 = tts?.setLanguage(Locale("en", "IN"))
                if (fallback1 == TextToSpeech.LANG_MISSING_DATA || fallback1 == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.US)
                }
            }
        } catch (e: Exception) {
            Log.w("TtsAnnouncementEngine", "Language set error", e)
        }
    }

    fun buildAnnouncementText(
        amount: Double,
        customerName: String,
        upiApp: UpiApp,
        settings: VoiceSettings
    ): String {
        val formattedAmount = if (amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", amount)
        }

        val appName = upiApp.displayName
        val cleanCustomer = customerName.trim().ifEmpty { "Customer" }

        return when (settings.language) {
            VoiceLanguage.HINDI -> {
                "$appName पर $cleanCustomer से $formattedAmount रुपये प्राप्त हुए।"
            }
            VoiceLanguage.ENGLISH -> {
                "Received $formattedAmount Rupees on $appName from $cleanCustomer."
            }
            VoiceLanguage.HINGLISH -> {
                "$appName par $cleanCustomer se $formattedAmount Rupees praapt hue."
            }
            VoiceLanguage.MARATHI -> {
                "$appName वर $cleanCustomer कडून $formattedAmount रुपये प्राप्त झाले."
            }
            VoiceLanguage.TAMIL -> {
                "$appName மூலம் $cleanCustomer இடமிருந்து $formattedAmount ரூபாய் பெறப்பட்டது."
            }
            VoiceLanguage.TELUGU -> {
                "$appName ద్వారా $cleanCustomer నుండి $formattedAmount రూపాయలు అందుకున్నారు."
            }
            VoiceLanguage.GUJARATI -> {
                "$appName પર $cleanCustomer તરફથી $formattedAmount રૂપિયા મળ્યા."
            }
            VoiceLanguage.BENGALI -> {
                "$appName এ $cleanCustomer এর কাছ থেকে $formattedAmount টাকা পাওয়া গেছে।"
            }
            VoiceLanguage.KANNADA -> {
                "$appName ಮೂಲಕ $cleanCustomer ಅವರಿಂದ $formattedAmount ರೂಪಾಯಿ ಸ್ವೀಕರಿಸಲಾಗಿದೆ."
            }
            VoiceLanguage.PUNJABI -> {
                "$appName 'ਤੇ $cleanCustomer ਤੋਂ $formattedAmount ਰੁਪਏ ਪ੍ਰਾਪਤ ਹੋਏ।"
            }
        }
    }

    fun playSoundboxChime() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 240)
            scope.launch {
                delay(260)
                try {
                    toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 280)
                    delay(300)
                    toneGen.release()
                } catch (e: Exception) {
                    // Ignore release errors
                }
            }
        } catch (e: Exception) {
            Log.w("TtsAnnouncementEngine", "Chime failed: ${e.message}")
        }
    }

    fun announcePayment(
        transaction: PaymentTransaction,
        settings: VoiceSettings,
        onStart: (() -> Unit)? = null
    ) {
        val text = buildAnnouncementText(
            amount = transaction.amount,
            customerName = transaction.customerName,
            upiApp = transaction.upiApp,
            settings = settings
        )
        speakText(text, settings, onStart)
    }

    fun speakText(
        text: String,
        settings: VoiceSettings,
        onStart: (() -> Unit)? = null
    ) {
        // Cancel any currently running speech animation job
        activeSpeechJob?.cancel()

        activeSpeechJob = scope.launch {
            // Immediate state change so UI displays speaking state instantly
            onStart?.invoke()
            onAnnouncementStarted?.invoke(text)
            onSpeechStateChanged?.invoke(true, text)

            // Acquire wake lock to prevent screen sleep dropping audio
            var wakeLock: PowerManager.WakeLock? = null
            try {
                wakeLock = powerManager?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "payalert:soundbox_tts"
                )
                wakeLock?.acquire(6000L)
            } catch (e: Exception) {
                Log.w("TtsAnnouncementEngine", "WakeLock warning", e)
            }

            // Volume boost if requested
            if (settings.volumeBoost && audioManager != null) {
                try {
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val targetVolume = (maxVolume * 0.95f).toInt()
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
                } catch (e: Exception) {
                    Log.w("TtsAnnouncementEngine", "Volume boost warning", e)
                }
            }

            // Play electronic soundbox chime before speech if enabled
            if (settings.playChimeBefore) {
                playSoundboxChime()
                delay(400)
            }

            // Speak via Android TextToSpeech engine
            if (isTtsReady && tts != null) {
                try {
                    setLanguage(settings.language)
                    tts?.setSpeechRate(settings.speechRate)
                    tts?.setPitch(settings.pitch)

                    val utteranceId = "utterance_${System.currentTimeMillis()}"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val bundle = Bundle()
                        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
                        bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, utteranceId)
                    } else {
                        val params = HashMap<String, String>()
                        params[TextToSpeech.Engine.KEY_PARAM_STREAM] = AudioManager.STREAM_MUSIC.toString()
                        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params)
                    }

                    if (settings.repeatTwice) {
                        delay(2800)
                        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "${utteranceId}_repeat")
                    }
                } catch (e: Exception) {
                    Log.e("TtsAnnouncementEngine", "Speech execution error", e)
                }
            } else {
                Log.i("TtsAnnouncementEngine", "Simulating speech display for: $text")
            }

            // Keep visual active speaking state alive for 3.5 seconds
            delay(3500)
            onSpeechStateChanged?.invoke(false, "")

            try {
                if (wakeLock?.isHeld == true) {
                    wakeLock.release()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun stop() {
        try {
            activeSpeechJob?.cancel()
            tts?.stop()
            onSpeechStateChanged?.invoke(false, "")
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun shutdown() {
        try {
            activeSpeechJob?.cancel()
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
