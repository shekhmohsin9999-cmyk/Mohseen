package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UpiApp
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
fun UpiSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val notifSettings by viewModel.notificationSettings.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()

    var showTestAlertSuccess by remember { mutableStateOf(false) }

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
                    text = "UPI Notification Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "Manage auto-detection for Google Pay, PhonePe, and Paytm",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
            }
        }

        // ==============================================================
        // 1. SYSTEM SERVICE & PERMISSION CHECKLIST
        // ==============================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upi_permissions_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
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
                                    .background(if (isServiceActive) SuccessGreen.copy(alpha = 0.2f) else AccentGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isServiceActive) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = if (isServiceActive) SuccessGreen else AccentGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isServiceActive) "Notification Listener Active" else "Permission Required",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isServiceActive) "Background voice listener connected" else "Grant access to read payment SMS/alerts",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.openSetupInstructions() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HelpOutline,
                                contentDescription = "Setup Guide",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons to open Android System Settings and Guide
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.openSetupInstructions() },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isServiceActive) NavySurface else SuccessGreenDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_notification_access_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Setup Guide & Access",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.toggleServiceSimulation()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_service_simulation_button")
                        ) {
                            Icon(
                                imageVector = if (isServiceActive) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (isServiceActive) SuccessGreen else AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isServiceActive) "Connected" else "Connect",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // ==============================================================
        // 2. UPI APPS SUPPORT TOGGLES (GPay, PhonePe, Paytm, BHIM)
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Supported UPI Apps",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )

                    // PhonePe
                    UpiAppToggleRow(
                        app = UpiApp.PHONEPE,
                        subtitle = "Auto-announces payments received via PhonePe QR",
                        enabled = notifSettings.phonepeEnabled,
                        onToggle = {
                            viewModel.updateNotificationSettings(notifSettings.copy(phonepeEnabled = it))
                        }
                    )

                    // Google Pay
                    UpiAppToggleRow(
                        app = UpiApp.GPAY,
                        subtitle = "Auto-announces payments received via Google Pay for Business",
                        enabled = notifSettings.gpayEnabled,
                        onToggle = {
                            viewModel.updateNotificationSettings(notifSettings.copy(gpayEnabled = it))
                        }
                    )

                    // Paytm
                    UpiAppToggleRow(
                        app = UpiApp.PAYTM,
                        subtitle = "Auto-announces payments received via Paytm Merchant QR",
                        enabled = notifSettings.paytmEnabled,
                        onToggle = {
                            viewModel.updateNotificationSettings(notifSettings.copy(paytmEnabled = it))
                        }
                    )

                    // BHIM
                    UpiAppToggleRow(
                        app = UpiApp.BHIM,
                        subtitle = "Auto-announces NPCI BHIM & bank UPI payments",
                        enabled = notifSettings.bhimEnabled,
                        onToggle = {
                            viewModel.updateNotificationSettings(notifSettings.copy(bhimEnabled = it))
                        }
                    )
                }
            }
        }

        // ==============================================================
        // 3. ANNOUNCEMENT RULES & MINIMUM AMOUNT FILTER
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Voice Announcement Rules",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )

                    // Speak Customer Name Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Announce Customer Name",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "Speaks sender name from UPI notification (e.g. \"from Rahul\")",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                        Switch(
                            checked = notifSettings.speakCustomerName,
                            onCheckedChange = {
                                viewModel.updateNotificationSettings(notifSettings.copy(speakCustomerName = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreenDark
                            ),
                            modifier = Modifier.testTag("toggle_speak_customer_name")
                        )
                    }

                    // Speak UPI App Name Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Announce UPI App Name",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "Prefixes with \"on PhonePe\" or \"on Google Pay\"",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                        Switch(
                            checked = notifSettings.speakUpiApp,
                            onCheckedChange = {
                                viewModel.updateNotificationSettings(notifSettings.copy(speakUpiApp = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreenDark
                            ),
                            modifier = Modifier.testTag("toggle_speak_upi_app")
                        )
                    }

                    // Minimum Amount Threshold Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Minimum Alert Amount",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "₹${notifSettings.minAmountThreshold.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary
                            )
                        }
                        Text(
                            text = "Ignore micropayments below this threshold if desired",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )

                        Slider(
                            value = notifSettings.minAmountThreshold.toFloat(),
                            onValueChange = {
                                viewModel.updateNotificationSettings(notifSettings.copy(minAmountThreshold = it.toDouble()))
                            },
                            valueRange = 1f..100f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = NavySecondary,
                                activeTrackColor = NavySecondary
                            ),
                            modifier = Modifier.testTag("min_amount_slider")
                        )
                    }
                }
            }
        }

        // ==============================================================
        // 4. TEST NOTIFICATION TRIGGER BUTTON
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
                    Text(
                        text = "Notification Simulator",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )
                    Text(
                        text = "Simulate an incoming notification payload to test parser & speaker output.",
                        fontSize = 12.sp,
                        color = SlateTextMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.triggerDemoPayment(350.0, "Pooja Patel", UpiApp.PHONEPE)
                            showTestAlertSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_test_notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simulate PhonePe ₹350 Notification", color = Color.White)
                    }

                    if (showTestAlertSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Alert triggered: playing chime and voice announcement now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreenDark
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun UpiAppToggleRow(
    app: UpiApp,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(app.brandColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.shortName.take(2).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = app.brandColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = app.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
            }
        }

        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = app.brandColor
            )
        )
    }
}
