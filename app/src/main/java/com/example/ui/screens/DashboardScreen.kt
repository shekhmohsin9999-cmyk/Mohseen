package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.ui.MainViewModel
import com.example.ui.components.PaymentTransactionCard
import com.example.ui.components.SoundWaveBars
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToVoiceSettings: () -> Unit,
    onNavigateToUpiSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val currentSpokenText by viewModel.currentSpokenText.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val metrics by viewModel.dashboardMetrics.collectAsState()

    var showSimulateDialog by remember { mutableStateOf(false) }
    var selectedTransactionForDetail by remember { mutableStateOf<PaymentTransaction?>(null) }

    val totalAmount = metrics.first
    val totalCount = metrics.second
    val appBreakdown = metrics.third

    val formattedTotalAmount = if (totalAmount % 1.0 == 0.0) {
        String.format(Locale.US, "%,d", totalAmount.toLong())
    } else {
        String.format(Locale.US, "%,.2f", totalAmount)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ==============================================================
        // 1. TOTAL PAYMENTS TODAY & TOTAL AMOUNT HERO CARD
        // ==============================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(NavyCard, NavyDark)
                            )
                        )
                        .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
                        .padding(18.dp)
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
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TODAY'S COLLECTION",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 1.sp
                                )
                            }

                            // Soundbox Voice Status Pill -> opens Voice customization
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavySurface)
                                    .clickable(onClick = { viewModel.openCustomVoiceModal() })
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("dashboard_voice_pill"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RecordVoiceOver,
                                    contentDescription = "Custom Voice",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = voiceSettings.language.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Total Payment Amount
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "₹",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTotalAmount,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Total Payments Today count
                        Text(
                            text = "$totalCount Payments Received Today",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // App-wise breakdown pills (clickable -> opens History filtered by that app)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppBreakdownItem(
                                app = UpiApp.PHONEPE,
                                amount = appBreakdown[UpiApp.PHONEPE] ?: 0.0,
                                onClick = { viewModel.navigateToHistoryWithFilter(UpiApp.PHONEPE) },
                                modifier = Modifier.weight(1f)
                            )
                            AppBreakdownItem(
                                app = UpiApp.GPAY,
                                amount = appBreakdown[UpiApp.GPAY] ?: 0.0,
                                onClick = { viewModel.navigateToHistoryWithFilter(UpiApp.GPAY) },
                                modifier = Modifier.weight(1f)
                            )
                            AppBreakdownItem(
                                app = UpiApp.PAYTM,
                                amount = appBreakdown[UpiApp.PAYTM] ?: 0.0,
                                onClick = { viewModel.navigateToHistoryWithFilter(UpiApp.PAYTM) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // ==============================================================
        // 2. QUICK DEMO / TEST VOICE ANNOUNCEMENT BAR
        // Non-overflowing clean layout with high-touch targets
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
                        .padding(14.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Voice Test",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Test Voice Alert",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextDark
                            )
                        }

                        // Subtitle actions row (Custom Voice & + Add Test)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavySecondary.copy(alpha = 0.08f))
                                    .clickable { viewModel.openCustomVoiceModal() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("custom_voice_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Tune,
                                        contentDescription = null,
                                        tint = NavySecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Custom",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavySecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessGreenLight)
                                    .clickable { showSimulateDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("custom_test_payment_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = SuccessGreenDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "New Test",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreenDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap a preset to speak instant voice announcement:",
                        fontSize = 11.sp,
                        color = SlateTextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset buttons with vertical layout (guaranteed never to wrap/overflow)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetVoiceButton(
                            amount = "₹100",
                            app = "Google Pay",
                            color = UpiApp.GPAY.brandColor,
                            onClick = {
                                viewModel.triggerDemoPayment(100.0, "Amit Patel", UpiApp.GPAY)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preset_100_gpay")
                        )
                        PresetVoiceButton(
                            amount = "₹500",
                            app = "PhonePe",
                            color = UpiApp.PHONEPE.brandColor,
                            onClick = {
                                viewModel.triggerDemoPayment(500.0, "Rahul Sharma", UpiApp.PHONEPE)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preset_500_phonepe")
                        )
                        PresetVoiceButton(
                            amount = "₹1,200",
                            app = "Paytm",
                            color = UpiApp.PAYTM.brandColor,
                            onClick = {
                                viewModel.triggerDemoPayment(1200.0, "Sneha Verma", UpiApp.PAYTM)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preset_1200_paytm")
                        )
                    }

                    // Live active announcement wave feedback
                    if (isSpeaking) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreenLight)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    SoundWaveBars(isAnimating = true)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (currentSpokenText.isNotEmpty()) currentSpokenText else "Speaking voice alert...",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreenDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { viewModel.stopSpeaking() }
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Stop,
                                        contentDescription = "Stop",
                                        tint = Color.Red,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==============================================================
        // 3. SOUNDBOX BACKGROUND STATUS & NOTIFICATION LISTENER BANNER
        // Clicking opens the clear setup instructions screen (Req 8)
        // ==============================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { viewModel.openSetupInstructions() })
                    .testTag("setup_notification_listener_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isServiceActive) SuccessGreenLight else Color(0xFFEFF6FF)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isServiceActive) SuccessGreen else NavySecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isServiceActive) Icons.Filled.CheckCircle else Icons.Filled.NotificationsActive,
                                contentDescription = "Status",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isServiceActive) "Live Soundbox Active" else "Setup Notification Listener",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isServiceActive) SuccessGreenDark else NavyPrimary
                            )
                            Text(
                                text = if (isServiceActive) "Tap for native Android listener guide & test" else "Tap to open setup instructions screen",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Configure",
                        tint = SlateTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ==============================================================
        // 4. RECENT TRANSACTIONS HEADER & LIST
        // ==============================================================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )

                // View All button opens complete transaction list (Req 9)
                TextButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.testTag("view_all_transactions_button")
                ) {
                    Text(
                        text = "View All (${transactions.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary
                    )
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "No payments",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No payments recorded yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark
                        )
                        Text(
                            text = "Use the quick test buttons above to simulate a transaction.",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    }
                }
            }
        } else {
            items(transactions.take(8), key = { it.id }) { txn ->
                PaymentTransactionCard(
                    transaction = txn,
                    onSpeakClick = { viewModel.repeatPayment(txn) },
                    onClick = { selectedTransactionForDetail = txn }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Modal Dialog: Custom Test Payment Simulation with input validation (Req 12)
    if (showSimulateDialog) {
        SimulatePaymentDialog(
            onDismiss = { showSimulateDialog = false },
            onConfirm = { amount, name, app ->
                viewModel.triggerDemoPayment(amount, name, app)
                showSimulateDialog = false
            }
        )
    }

    // Modal Dialog: Transaction Detail Receipt
    selectedTransactionForDetail?.let { txn ->
        TransactionDetailDialog(
            transaction = txn,
            onDismiss = { selectedTransactionForDetail = null },
            onSpeak = {
                viewModel.repeatPayment(txn)
            }
        )
    }
}

@Composable
fun PresetVoiceButton(
    amount: String,
    app: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.5.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = app,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AppBreakdownItem(
    app: UpiApp,
    amount: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amountFormatted = if (amount % 1.0 == 0.0) {
        String.format(Locale.US, "%,d", amount.toLong())
    } else {
        String.format(Locale.US, "%,.1f", amount)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(NavySurface)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        Column {
            Text(
                text = app.shortName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = app.brandColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "₹$amountFormatted",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatePaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, customerName: String, app: UpiApp) -> Unit
) {
    var amountInput by remember { mutableStateOf("500") }
    var customerNameInput by remember { mutableStateOf("Rahul Sharma") }
    var selectedApp by remember { mutableStateOf(UpiApp.PHONEPE) }
    var appDropdownExpanded by remember { mutableStateOf(false) }

    var amountError by remember { mutableStateOf<String?>(null) }
    var customerError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Simulate UPI Payment Voice Alert",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextDark
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Triggers a live merchant soundbox announcement using Android TextToSpeech engine with sound chime.",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = {
                        amountInput = it
                        amountError = null
                    },
                    label = { Text("Amount (₹)") },
                    prefix = { Text("₹ ") },
                    isError = amountError != null,
                    supportingText = amountError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_amount_input")
                )

                OutlinedTextField(
                    value = customerNameInput,
                    onValueChange = {
                        customerNameInput = it
                        customerError = null
                    },
                    label = { Text("Customer Name") },
                    isError = customerError != null,
                    supportingText = customerError?.let { { Text(it, color = Color.Red, fontSize = 11.sp) } },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_customer_input")
                )

                // UPI App selector
                ExposedDropdownMenuBox(
                    expanded = appDropdownExpanded,
                    onExpandedChange = { appDropdownExpanded = !appDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedApp.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment UPI App") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = appDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = appDropdownExpanded,
                        onDismissRequest = { appDropdownExpanded = false }
                    ) {
                        listOf(UpiApp.PHONEPE, UpiApp.GPAY, UpiApp.PAYTM, UpiApp.BHIM).forEach { app ->
                            DropdownMenuItem(
                                text = { Text(app.displayName) },
                                onClick = {
                                    selectedApp = app
                                    appDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull()
                    var hasError = false
                    if (amount == null || amount <= 0) {
                        amountError = "Please enter a valid amount greater than ₹0"
                        hasError = true
                    }
                    if (customerNameInput.trim().isEmpty()) {
                        customerError = "Customer name is required"
                        hasError = true
                    }
                    if (!hasError && amount != null) {
                        onConfirm(amount, customerNameInput.trim(), selectedApp)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                modifier = Modifier.testTag("confirm_simulate_payment_button")
            ) {
                Text("Announce Voice Alert", color = Color.White)
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
fun TransactionDetailDialog(
    transaction: PaymentTransaction,
    onDismiss: () -> Unit,
    onSpeak: () -> Unit
) {
    val amountFormatted = if (transaction.amount % 1.0 == 0.0) {
        String.format(Locale.US, "%,d", transaction.amount.toLong())
    } else {
        String.format(Locale.US, "%,.2f", transaction.amount)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Payment Receipt",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccessGreenLight)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SUCCESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreenDark
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Large Amount
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SuccessGreenLight)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Amount Received",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SuccessGreenDark
                        )
                        Text(
                            text = "₹$amountFormatted",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = SuccessGreenDark
                        )
                    }
                }

                ReceiptRow(label = "Customer Name", value = transaction.customerName)
                ReceiptRow(label = "UPI Application", value = transaction.upiApp.displayName)
                ReceiptRow(label = "UPI Ref ID", value = transaction.upiRefId)
                ReceiptRow(label = "Transaction ID", value = transaction.id)
                ReceiptRow(
                    label = "Date & Time",
                    value = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()).format(java.util.Date(transaction.timestamp))
                )
                ReceiptRow(label = "Status", value = "Credited to Merchant Account")
            }
        },
        confirmButton = {
            Button(
                onClick = onSpeak,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("receipt_dialog_speak_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Re-announce Voice", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = SlateTextMuted)
            }
        }
    )
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = SlateTextMuted
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SlateTextDark
        )
    }
}
