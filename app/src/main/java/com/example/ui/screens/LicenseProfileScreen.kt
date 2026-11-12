package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MerchantProfile
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentGold
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight

@Composable
fun LicenseProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val merchantProfile by viewModel.merchantProfile.collectAsState()
    val licenseStatus by viewModel.licenseStatus.collectAsState()
    val isActivating by viewModel.isActivatingLicense.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showActivationKeyDialog by remember { mutableStateOf(false) }
    var showUpiQrDialog by remember { mutableStateOf(false) }
    var activationStatusMessage by remember { mutableStateOf<String?>(null) }
    var isActivationError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "Profile & License Activation",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "Shopkeeper identity and ₹100 merchant license",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
            }
        }

        // ==============================================================
        // 1. ₹100 LIFETIME LICENSE ACTIVATION HERO
        // ==============================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("license_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(NavyCard, NavyDark))
                        )
                        .border(
                            1.dp,
                            if (licenseStatus.isActivated) SuccessGreen.copy(alpha = 0.5f) else AccentGold.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (licenseStatus.isActivated) SuccessGreen.copy(alpha = 0.2f) else AccentGold.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (licenseStatus.isActivated) Icons.Filled.CheckCircle else Icons.Filled.Security,
                                        contentDescription = null,
                                        tint = if (licenseStatus.isActivated) SuccessGreen else AccentGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = licenseStatus.planName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (licenseStatus.isActivated) "One-time ₹100 License Active" else "Trial Period (${licenseStatus.trialDaysRemaining} days remaining)",
                                        fontSize = 12.sp,
                                        color = if (licenseStatus.isActivated) SuccessGreen else AccentGold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (licenseStatus.isActivated) SuccessGreen else AccentGold)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (licenseStatus.isActivated) "PRO ACTIVE" else "TRIAL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NavyDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Demo Mode Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(NavySurface)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Demo Mode Validation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (licenseStatus.isDemoMode) "Validates demo keys (PAYALERT100)" else "Production gateway mode",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Switch(
                                checked = licenseStatus.isDemoMode,
                                onCheckedChange = { viewModel.toggleDemoMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = SuccessGreenDark
                                ),
                                modifier = Modifier.testTag("demo_mode_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // License Details Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavySurface)
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ProfileInfoRow(label = "License Key", value = licenseStatus.licenseKey, isDark = true)
                                ProfileInfoRow(label = "Merchant ID", value = licenseStatus.merchantId, isDark = true)
                                ProfileInfoRow(label = "Device ID", value = licenseStatus.deviceId, isDark = true)
                                ProfileInfoRow(label = "Fee", value = "₹100 (Lifetime Unlimited)", isDark = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Pay ₹100 UPI / Enter Key
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showUpiQrDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isActivating,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pay_100_activation_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pay ₹100 UPI", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showActivationKeyDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isActivating,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("enter_license_key_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Key,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enter Key", color = Color.White)
                            }
                        }

                        // Reset to Trial test button
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.resetLicenseToTrial()
                                    activationStatusMessage = "License reset to Trial Mode"
                                    isActivationError = false
                                },
                                modifier = Modifier.testTag("reset_to_trial_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reset to Trial Mode (Test flow)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        if (isActivating) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = SuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Verifying merchant license key...",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }

                        activationStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isActivationError) "⚠ $msg" else "✓ $msg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActivationError) Color(0xFFF87171) else SuccessGreen
                            )
                        }
                    }
                }
            }
        }

        // ==============================================================
        // 2. MERCHANT PROFILE DETAILS CARD
        // ==============================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = "Shop",
                                tint = NavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Merchant Profile",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextDark
                            )
                        }

                        // Working Edit Profile button
                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_merchant_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = NavySecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProfileInfoRow(label = "Shop Name", value = merchantProfile.shopName)
                        ProfileInfoRow(label = "Shop Owner", value = merchantProfile.ownerName)
                        ProfileInfoRow(label = "Merchant UPI ID", value = merchantProfile.upiId)
                        ProfileInfoRow(label = "Phone Number", value = merchantProfile.phoneNumber)
                        ProfileInfoRow(label = "City / State", value = merchantProfile.city)
                        ProfileInfoRow(label = "Soundbox Model", value = merchantProfile.soundboxSerial)
                    }
                }
            }
        }

        // ==============================================================
        // 3. ARCHITECTURE & BACKEND EXTENSIONS
        // ==============================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Business,
                            contentDescription = "Architecture",
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Native Android & Backend Ready",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark
                        )
                    }

                    Text(
                        text = "• NotificationListenerService natively processes GPay, PhonePe & Paytm\n• Modular architecture ready for Firebase Cloud Firestore ledger backup\n• Admin panel license verification with ₹100 activation key system\n• Zero third-party audio latency using Android TextToSpeech & WakeLock",
                        fontSize = 12.sp,
                        color = SlateTextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // ==============================================================
        // 4. MERCHANT SUPPORT CONTACTS (Working Call/Email/WhatsApp)
        // ==============================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.SupportAgent,
                            contentDescription = "Support",
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Merchant Helpdesk",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark
                        )
                    }

                    // Phone Dial action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Fallback: copy to clipboard
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Helpdesk Phone", "+91 98765 43210"))
                                }
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = null,
                                tint = SuccessGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = licenseStatus.supportPhone,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                        }

                        Text(
                            text = "Call 24x7",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreenDark
                        )
                    }

                    // Email / WhatsApp action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@payalert.in"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Helpdesk Email", "support@payalert.in"))
                                }
                            }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = null,
                                tint = NavySecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = licenseStatus.supportEmail,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                        }

                        Text(
                            text = "Email / WhatsApp",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Dialog: Edit Merchant Profile with Form Validation (Req 12)
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = merchantProfile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateMerchantProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    // Dialog: Enter License Key with Demo Mode validation (Req 13)
    if (showActivationKeyDialog) {
        ActivationKeyDialog(
            isDemoMode = licenseStatus.isDemoMode,
            onDismiss = { showActivationKeyDialog = false },
            onActivate = { key ->
                showActivationKeyDialog = false
                viewModel.activateLicense(key) { result ->
                    result.onSuccess { msg ->
                        activationStatusMessage = msg
                        isActivationError = false
                    }.onFailure { err ->
                        activationStatusMessage = err.message
                        isActivationError = true
                    }
                }
            }
        )
    }

    // Dialog: UPI ₹100 QR & Payment Flow
    if (showUpiQrDialog) {
        UpiPaymentQrDialog(
            vpa = licenseStatus.upiPaymentVpa,
            onDismiss = { showUpiQrDialog = false },
            onPaymentDone = {
                showUpiQrDialog = false
                viewModel.activateLicense("PAYALERT100") { result ->
                    result.onSuccess {
                        activationStatusMessage = "₹100 UPI Payment Verified: Lifetime Pro Active!"
                        isActivationError = false
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, isDark: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isDark) Color(0xFF94A3B8) else SlateTextMuted
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else SlateTextDark
        )
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: MerchantProfile,
    onDismiss: () -> Unit,
    onSave: (MerchantProfile) -> Unit
) {
    var shopName by remember { mutableStateOf(currentProfile.shopName) }
    var ownerName by remember { mutableStateOf(currentProfile.ownerName) }
    var upiId by remember { mutableStateOf(currentProfile.upiId) }
    var phone by remember { mutableStateOf(currentProfile.phoneNumber) }
    var city by remember { mutableStateOf(currentProfile.city) }

    var shopNameError by remember { mutableStateOf<String?>(null) }
    var ownerNameError by remember { mutableStateOf<String?>(null) }
    var upiIdError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Merchant Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = {
                        shopName = it
                        shopNameError = null
                    },
                    label = { Text("Shop Name") },
                    isError = shopNameError != null,
                    supportingText = shopNameError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_shop_name_input")
                )
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = {
                        ownerName = it
                        ownerNameError = null
                    },
                    label = { Text("Owner / Merchant Name") },
                    isError = ownerNameError != null,
                    supportingText = ownerNameError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_owner_name_input")
                )
                OutlinedTextField(
                    value = upiId,
                    onValueChange = {
                        upiId = it
                        upiIdError = null
                    },
                    label = { Text("Merchant UPI ID / VPA") },
                    placeholder = { Text("name@bank") },
                    isError = upiIdError != null,
                    supportingText = upiIdError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_upi_id_input")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = null
                    },
                    label = { Text("Contact Phone") },
                    isError = phoneError != null,
                    supportingText = phoneError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_phone_input")
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City, State") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var valid = true
                    if (shopName.trim().isEmpty()) {
                        shopNameError = "Shop name cannot be blank"
                        valid = false
                    }
                    if (ownerName.trim().isEmpty()) {
                        ownerNameError = "Owner name cannot be blank"
                        valid = false
                    }
                    if (upiId.trim().isEmpty() || !upiId.contains("@")) {
                        upiIdError = "Enter valid UPI ID (e.g. store@okhdfcbank)"
                        valid = false
                    }
                    if (phone.trim().length < 8) {
                        phoneError = "Enter valid phone number"
                        valid = false
                    }
                    if (valid) {
                        onSave(
                            currentProfile.copy(
                                shopName = shopName.trim(),
                                ownerName = ownerName.trim(),
                                upiId = upiId.trim(),
                                phoneNumber = phone.trim(),
                                city = city.trim()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextMuted)
            }
        }
    )
}

@Composable
fun ActivationKeyDialog(
    isDemoMode: Boolean,
    onDismiss: () -> Unit,
    onActivate: (String) -> Unit
) {
    var keyInput by remember { mutableStateOf(if (isDemoMode) "PAYALERT100" else "") }
    var keyError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Enter ₹100 License Key",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isDemoMode) {
                        "Demo Mode Active: Enter PAYALERT100 or PA-DEMO-2026 to activate in demo mode."
                    } else {
                        "Enter the 16-character production license key provided by your PAY ALERT distributor:"
                    },
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = {
                        keyInput = it.uppercase()
                        keyError = null
                    },
                    label = { Text("License Key") },
                    placeholder = { Text("PAYALERT100") },
                    isError = keyError != null,
                    supportingText = keyError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activation_key_input")
                )

                if (isDemoMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TextButton(
                            onClick = { keyInput = "PAYALERT100" }
                        ) {
                            Text("Use: PAYALERT100", fontSize = 11.sp, color = SuccessGreenDark)
                        }
                        TextButton(
                            onClick = { keyInput = "PA-DEMO-2026" }
                        ) {
                            Text("Use: PA-DEMO-2026", fontSize = 11.sp, color = NavySecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (keyInput.trim().isEmpty()) {
                        keyError = "License key is required"
                    } else {
                        onActivate(keyInput.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                modifier = Modifier.testTag("dialog_activate_key_button")
            ) {
                Text("Verify & Activate", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextMuted)
            }
        }
    )
}

@Composable
fun UpiPaymentQrDialog(
    vpa: String,
    onDismiss: () -> Unit,
    onPaymentDone: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pay ₹100 Lifetime License",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextDark
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Scan QR using any UPI App (GPay, PhonePe, Paytm, BHIM) to activate your merchant soundbox:",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )

                // Simulated QR Graphic Box
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, NavySecondary, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.QrCode2,
                            contentDescription = "UPI QR Code",
                            tint = NavyDark,
                            modifier = Modifier.size(110.dp)
                        )
                        Text(
                            text = "Amount: ₹100.00",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreenDark
                        )
                    }
                }

                // UPI VPA row with copy button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = vpa,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTextDark
                    )
                    IconButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("Merchant VPA", vpa))
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy UPI ID",
                            tint = NavySecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPaymentDone,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                modifier = Modifier.testTag("confirm_upi_payment_done_button")
            ) {
                Text("I have paid ₹100 (Activate)", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextMuted)
            }
        }
    )
}
