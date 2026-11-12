package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.ui.theme.AccentGold
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PayAlertTopBar(
    shopName: String,
    isServiceActive: Boolean,
    isSpeaking: Boolean,
    isLicenseActive: Boolean,
    onTestSoundClick: () -> Unit,
    onShopNameClick: () -> Unit = {},
    onStatusBadgeClick: () -> Unit = {},
    onLicenseBadgeClick: () -> Unit = {},
    onLogoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NavyDark,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
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
                    // App Logo (Clickable -> plays chime)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NavySecondary, NavyCard)
                                )
                            )
                            .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onLogoClick)
                            .testTag("topbar_app_logo"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PAY ALERT",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Soundbox Beacon Pill (Clickable -> opens status / voice)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isServiceActive) SuccessGreen.copy(alpha = 0.2f) else AccentGold.copy(alpha = 0.2f))
                                    .clickable(onClick = onStatusBadgeClick)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("topbar_status_beacon")
                            ) {
                                Text(
                                    text = if (isServiceActive) "VOICE READY" else "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isServiceActive) SuccessGreen else AccentGold
                                )
                            }
                        }
                        Text(
                            text = shopName,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clickable(onClick = onShopNameClick)
                                .testTag("topbar_shop_info")
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // License Status Chip (Clickable -> opens License Screen)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isLicenseActive) SuccessGreen.copy(alpha = 0.15f) else AccentGold.copy(alpha = 0.15f))
                            .border(
                                1.dp,
                                if (isLicenseActive) SuccessGreen.copy(alpha = 0.4f) else AccentGold.copy(alpha = 0.4f),
                                CircleShape
                            )
                            .clickable(onClick = onLicenseBadgeClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("topbar_license_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLicenseActive) Icons.Filled.CheckCircle else Icons.Filled.Security,
                                contentDescription = "License",
                                tint = if (isLicenseActive) SuccessGreen else AccentGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLicenseActive) "PRO" else "TRIAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLicenseActive) SuccessGreen else AccentGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Audio speaker test button with animation if speaking
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSpeaking) SuccessGreen else NavySurface)
                            .clickable(onClick = onTestSoundClick)
                            .testTag("topbar_sound_test_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Filled.NotificationsActive else Icons.Filled.VolumeUp,
                            contentDescription = "Test Audio",
                            tint = if (isSpeaking) NavyDark else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isSpeaking) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SoundWaveBars(isAnimating = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Announcing payment voice alert...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SoundWaveBars(isAnimating: Boolean) {
    val transition = rememberInfiniteTransition(label = "soundwave")
    val h1 by transition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 18f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 8f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "h3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isAnimating) h1.dp else 8.dp)
                .clip(CircleShape)
                .background(SuccessGreen)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isAnimating) h2.dp else 14.dp)
                .clip(CircleShape)
                .background(SuccessGreen)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isAnimating) h3.dp else 10.dp)
                .clip(CircleShape)
                .background(SuccessGreen)
        )
    }
}

@Composable
fun UpiAppBadge(upiApp: UpiApp) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(upiApp.brandColor.copy(alpha = 0.12f))
            .border(1.dp, upiApp.brandColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = upiApp.displayName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = upiApp.brandColor
        )
    }
}

@Composable
fun PaymentTransactionCard(
    transaction: PaymentTransaction,
    onSpeakClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = formatTimestamp(transaction.timestamp)
    val amountFormatted = if (transaction.amount % 1.0 == 0.0) {
        transaction.amount.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", transaction.amount)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("txn_card_${transaction.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
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
                    // Green circular check icon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SuccessGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SuccessGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = transaction.customerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = timeFormatted,
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                            if (transaction.isDemo) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• DEMO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // Amount in vibrant bold green
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+ ₹$amountFormatted",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SuccessGreenDark
                    )
                    Text(
                        text = "SUCCESS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreenDark,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer row with UPI badge and quick voice repeat button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UpiAppBadge(upiApp = transaction.upiApp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = transaction.upiRefId,
                        fontSize = 10.sp,
                        color = SlateTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Single tap voice repeat button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySecondary.copy(alpha = 0.08f))
                        .clickable(onClick = onSpeakClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("speak_again_${transaction.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Speak Again",
                            tint = NavySecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Speak",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavySecondary
                        )
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)

    return when {
        diff < 60 * 1000 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}
