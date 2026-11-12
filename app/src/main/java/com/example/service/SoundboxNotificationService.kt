package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.PaymentAlertRepository
import com.example.model.PaymentStatus
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import java.util.UUID
import java.util.regex.Pattern

class SoundboxNotificationService : NotificationListenerService() {

    companion object {
        private const val TAG = "SoundboxNotifService"
        private const val CHANNEL_ID = "payalert_service_channel"
        private const val NOTIF_ID = 1001

        var isServiceRunning = false
            private set
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        isServiceRunning = true
        PaymentAlertRepository.updateServiceStatus(true)
        Log.i(TAG, "PAY ALERT Soundbox Notification Listener connected successfully")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isServiceRunning = false
        PaymentAlertRepository.updateServiceStatus(false)
        Log.w(TAG, "PAY ALERT Soundbox Notification Listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val pkg = sbn.packageName ?: return
        val upiApp = UpiApp.fromPackage(pkg)

        // Only process UPI and payment apps
        if (upiApp == UpiApp.OTHER && !pkg.contains("upi", ignoreCase = true) && !pkg.contains("pay", ignoreCase = true)) {
            return
        }

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
        val combined = "$title $text $bigText"

        Log.d(TAG, "Inspecting notification from $pkg: $combined")

        // Parse payment received
        val parsed = parsePayment(combined, upiApp)
        if (parsed != null) {
            PaymentAlertRepository.recordIncomingPayment(applicationContext, parsed)
        }
    }

    private fun parsePayment(text: String, detectedApp: UpiApp): PaymentTransaction? {
        val lower = text.lowercase()
        // Must contain credit/received indicators
        val isReceived = lower.contains("received") ||
                lower.contains("credited") ||
                lower.contains("paid you") ||
                lower.contains("sent you") ||
                lower.contains("praapt") ||
                lower.contains("safalta")

        if (!isReceived) return null

        // Amount regex: ₹500, Rs. 500, INR 500, Rs 500
        val amountPattern = Pattern.compile("(?:₹|rs\\.?|inr)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE)
        val matcher = amountPattern.matcher(text)
        if (!matcher.find()) return null

        val amountStr = matcher.group(1)?.replace(",", "").orEmpty()
        val amount = amountStr.toDoubleOrNull() ?: return null

        // Sender extraction
        var senderName = "Customer"
        val fromPattern = Pattern.compile("(?:from|by|se)\\s+([A-Za-z\\s]{2,25})", Pattern.CASE_INSENSITIVE)
        val fromMatcher = fromPattern.matcher(text)
        if (fromMatcher.find()) {
            val name = fromMatcher.group(1)?.trim().orEmpty()
            if (name.isNotEmpty() && !name.equals("bank", ignoreCase = true) && !name.equals("account", ignoreCase = true)) {
                senderName = name
            }
        }

        val refId = "UPI/" + System.currentTimeMillis().toString().takeLast(8) + "/CR"

        return PaymentTransaction(
            id = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}",
            customerName = senderName,
            amount = amount,
            upiApp = detectedApp,
            timestamp = System.currentTimeMillis(),
            status = PaymentStatus.SUCCESS,
            upiRefId = refId,
            isDemo = false,
            isSpoken = false
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        PaymentAlertRepository.updateServiceStatus(false)
    }
}
