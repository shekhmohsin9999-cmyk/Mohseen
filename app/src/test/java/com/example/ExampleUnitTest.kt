package com.example

import com.example.data.PaymentAlertRepository
import com.example.model.PaymentStatus
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.model.VoiceLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testUpiAppDetection() {
        assertEquals(UpiApp.GPAY, UpiApp.fromPackage("com.google.android.apps.nbu.paisa.user"))
        assertEquals(UpiApp.PHONEPE, UpiApp.fromPackage("com.phonepe.app"))
        assertEquals(UpiApp.PAYTM, UpiApp.fromPackage("net.one97.paytm"))
        assertEquals(UpiApp.BHIM, UpiApp.fromPackage("in.org.npci.upiapp"))
    }

    @Test
    fun testPaymentTransactionCreation() {
        val txn = PaymentTransaction(
            id = "TXN-101",
            customerName = "Rahul Sharma",
            amount = 500.0,
            upiApp = UpiApp.PHONEPE,
            timestamp = System.currentTimeMillis(),
            status = PaymentStatus.SUCCESS,
            upiRefId = "UPI/998127391/CR"
        )
        assertEquals("Rahul Sharma", txn.customerName)
        assertEquals(500.0, txn.amount, 0.001)
        assertEquals(PaymentStatus.SUCCESS, txn.status)
    }

    @Test
    fun testVoiceLanguagesExist() {
        assertEquals(10, VoiceLanguage.values().size)
        assertNotNull(VoiceLanguage.HINDI)
        assertNotNull(VoiceLanguage.ENGLISH)
        assertNotNull(VoiceLanguage.HINGLISH)
        assertNotNull(VoiceLanguage.MARATHI)
        assertNotNull(VoiceLanguage.TAMIL)
        assertNotNull(VoiceLanguage.TELUGU)
        assertNotNull(VoiceLanguage.GUJARATI)
        assertNotNull(VoiceLanguage.BENGALI)
        assertNotNull(VoiceLanguage.KANNADA)
        assertNotNull(VoiceLanguage.PUNJABI)
    }

    @Test
    fun testDemoPaymentTrigger() {
        val prevCount = PaymentAlertRepository.transactions.value.size
        val newTxn = PaymentAlertRepository.triggerDemoPayment(
            amount = 750.0,
            customerName = "Vikram Malhotra",
            upiApp = UpiApp.PAYTM,
            speakImmediately = false
        )
        assertEquals("Vikram Malhotra", newTxn.customerName)
        assertEquals(750.0, newTxn.amount, 0.001)
        assertEquals(prevCount + 1, PaymentAlertRepository.transactions.value.size)
        assertTrue(PaymentAlertRepository.transactions.value.first().id.startsWith("DEMO-"))
    }
}
