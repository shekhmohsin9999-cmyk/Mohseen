package com.example.model

import java.util.Locale

enum class VoiceLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val locale: Locale,
    val sampleTextTemplate: String
) {
    HINDI(
        code = "hi",
        displayName = "Hindi",
        nativeName = "हिन्दी",
        locale = Locale("hi", "IN"),
        sampleTextTemplate = "%s पर %s से %s रुपये प्राप्त हुए।"
    ),
    ENGLISH(
        code = "en",
        displayName = "English (Indian)",
        nativeName = "English",
        locale = Locale("en", "IN"),
        sampleTextTemplate = "Received %s Rupees on %s from %s."
    ),
    HINGLISH(
        code = "hi-latn",
        displayName = "Hinglish",
        nativeName = "Hinglish",
        locale = Locale("hi", "IN"),
        sampleTextTemplate = "%s par %s se %s Rupees praapt hue."
    ),
    MARATHI(
        code = "mr",
        displayName = "Marathi",
        nativeName = "मराठी",
        locale = Locale("mr", "IN"),
        sampleTextTemplate = "%s वर %s कडून %s रुपये प्राप्त झाले."
    ),
    TAMIL(
        code = "ta",
        displayName = "Tamil",
        nativeName = "தமிழ்",
        locale = Locale("ta", "IN"),
        sampleTextTemplate = "%s மூலம் %s இடமிருந்து %s ரூபாய் பெறப்பட்டது."
    ),
    TELUGU(
        code = "te",
        displayName = "Telugu",
        nativeName = "తెలుగు",
        locale = Locale("te", "IN"),
        sampleTextTemplate = "%s ద్వారా %s నుండి %s రూపాయలు అందుకున్నారు."
    ),
    GUJARATI(
        code = "gu",
        displayName = "Gujarati",
        nativeName = "ગુજરાતી",
        locale = Locale("gu", "IN"),
        sampleTextTemplate = "%s પર %s તરફથી %s રૂપિયા મળ્યા."
    ),
    BENGALI(
        code = "bn",
        displayName = "Bengali",
        nativeName = "বাংলা",
        locale = Locale("bn", "IN"),
        sampleTextTemplate = "%s এ %s এর কাছ থেকে %s টাকা পাওয়া গেছে।"
    ),
    KANNADA(
        code = "kn",
        displayName = "Kannada",
        nativeName = "ಕನ್ನಡ",
        locale = Locale("kn", "IN"),
        sampleTextTemplate = "%s ಮೂಲಕ %s ಅವರಿಂದ %s ರೂಪಾಯಿ ಸ್ವೀಕರಿಸಲಾಗಿದೆ."
    ),
    PUNJABI(
        code = "pa",
        displayName = "Punjabi",
        nativeName = "ਪੰਜਾਬੀ",
        locale = Locale("pa", "IN"),
        sampleTextTemplate = "%s 'ਤੇ %s ਤੋਂ %s ਰੁਪਏ ਪ੍ਰਾਪਤ ਹੋਏ।"
    )
}

data class VoiceSettings(
    val language: VoiceLanguage = VoiceLanguage.HINDI,
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val volumeBoost: Boolean = true,
    val playChimeBefore: Boolean = true,
    val voiceGender: String = "Female",
    val repeatTwice: Boolean = false
)
