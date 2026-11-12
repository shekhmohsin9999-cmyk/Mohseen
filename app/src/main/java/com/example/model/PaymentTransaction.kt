package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ColorBhim
import com.example.ui.theme.ColorGPay
import com.example.ui.theme.ColorPaytm
import com.example.ui.theme.ColorPhonePe

enum class UpiApp(
    val displayName: String,
    val packageName: String,
    val shortName: String,
    val brandColor: Color
) {
    GPAY(
        displayName = "Google Pay",
        packageName = "com.google.android.apps.nbu.paisa.user",
        shortName = "GPay",
        brandColor = ColorGPay
    ),
    PHONEPE(
        displayName = "PhonePe",
        packageName = "com.phonepe.app",
        shortName = "PhonePe",
        brandColor = ColorPhonePe
    ),
    PAYTM(
        displayName = "Paytm",
        packageName = "net.one97.paytm",
        shortName = "Paytm",
        brandColor = ColorPaytm
    ),
    BHIM(
        displayName = "BHIM UPI",
        packageName = "in.org.npci.upiapp",
        shortName = "BHIM",
        brandColor = ColorBhim
    ),
    OTHER(
        displayName = "UPI Payment",
        packageName = "",
        shortName = "UPI",
        brandColor = Color(0xFF0F172A)
    );

    companion object {
        fun fromPackage(pkg: String): UpiApp {
            return when {
                pkg.contains("google", ignoreCase = true) || pkg.contains("paisa", ignoreCase = true) -> GPAY
                pkg.contains("phonepe", ignoreCase = true) -> PHONEPE
                pkg.contains("paytm", ignoreCase = true) -> PAYTM
                pkg.contains("bhim", ignoreCase = true) || pkg.contains("npci", ignoreCase = true) -> BHIM
                else -> OTHER
            }
        }
    }
}

enum class PaymentStatus {
    SUCCESS,
    PENDING,
    FAILED
}

data class PaymentTransaction(
    val id: String,
    val customerName: String,
    val amount: Double,
    val upiApp: UpiApp,
    val timestamp: Long,
    val status: PaymentStatus = PaymentStatus.SUCCESS,
    val upiRefId: String,
    val isDemo: Boolean = false,
    val isSpoken: Boolean = true
)
