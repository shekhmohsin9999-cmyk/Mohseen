package com.example.model

data class LicenseStatus(
    val isActivated: Boolean = true,
    val isDemoMode: Boolean = true,
    val licenseKey: String = "PA-8942-7810-DEMO",
    val merchantId: String = "MERCHANT-883921",
    val deviceId: String = "DEVICE-PA-99321",
    val planName: String = "Lifetime Merchant Pro",
    val priceInr: Int = 100,
    val activatedDate: Long = System.currentTimeMillis() - 86400000L * 15,
    val trialDaysRemaining: Int = 14,
    val supportPhone: String = "+91 98765 43210",
    val supportEmail: String = "support@payalert.in",
    val upiPaymentVpa: String = "payalert.merchant@okaxis",
    val lastValidationMessage: String = ""
)

data class MerchantProfile(
    val shopName: String = "Sharma General Store",
    val ownerName: String = "Mohsin Sheikh",
    val upiId: String = "sharmastore@okicici",
    val phoneNumber: String = "+91 98765 43210",
    val soundboxSerial: String = "PAYALERT-VOICE-BOX-2026",
    val city: String = "Mumbai, Maharashtra"
)

data class NotificationSupportSettings(
    val gpayEnabled: Boolean = true,
    val phonepeEnabled: Boolean = true,
    val paytmEnabled: Boolean = true,
    val bhimEnabled: Boolean = true,
    val minAmountThreshold: Double = 1.0,
    val autoWakeScreen: Boolean = true,
    val speakCustomerName: Boolean = true,
    val speakUpiApp: Boolean = true
)
