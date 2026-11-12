package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceLanguage
import com.example.ui.MainViewModel
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomVoiceDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val currentSettings by viewModel.voiceSettings.collectAsState()

    var selectedLang by remember { mutableStateOf(currentSettings.language) }
    var speed by remember { mutableFloatStateOf(currentSettings.speechRate) }
    var pitch by remember { mutableFloatStateOf(currentSettings.pitch) }
    var volumeBoost by remember { mutableStateOf(currentSettings.volumeBoost) }
    var playChime by remember { mutableStateOf(currentSettings.playChimeBefore) }
    var sampleAmount by remember { mutableStateOf("500") }
    var sampleCustomer by remember { mutableStateOf("Rahul Sharma") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Custom Voice",
                        tint = SuccessGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Custom Voice Tuning",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Language:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        VoiceLanguage.HINDI,
                        VoiceLanguage.ENGLISH,
                        VoiceLanguage.HINGLISH,
                        VoiceLanguage.MARATHI,
                        VoiceLanguage.TAMIL,
                        VoiceLanguage.GUJARATI
                    ).forEach { lang ->
                        val isSel = selectedLang == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) NavyPrimary else Color(0xFFF1F5F9))
                                .clickable { selectedLang = lang }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${lang.displayName}",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else SlateTextDark
                            )
                        }
                    }
                }

                // Speed Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Speed Rate", fontSize = 12.sp, color = SlateTextDark)
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", speed)}x",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    }
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.7f..1.5f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = NavySecondary,
                            activeTrackColor = NavySecondary
                        )
                    )
                }

                // Pitch Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Voice Pitch", fontSize = 12.sp, color = SlateTextDark)
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", pitch)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    }
                    Slider(
                        value = pitch,
                        onValueChange = { pitch = it },
                        valueRange = 0.7f..1.4f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = NavySecondary,
                            activeTrackColor = NavySecondary
                        )
                    )
                }

                // Chime switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Play Soundbox Chime", fontSize = 12.sp, color = SlateTextDark)
                    Switch(
                        checked = playChime,
                        onCheckedChange = { playChime = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SuccessGreenDark
                        )
                    )
                }

                // Test button right in dialog
                Button(
                    onClick = {
                        val tempSettings = currentSettings.copy(
                            language = selectedLang,
                            speechRate = speed,
                            pitch = pitch,
                            playChimeBefore = playChime
                        )
                        viewModel.updateVoiceSettings(tempSettings)
                        viewModel.testVoiceAnnouncement()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_voice_dialog_test_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Speak Sample Announcement Now", fontSize = 12.sp, color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = currentSettings.copy(
                        language = selectedLang,
                        speechRate = speed,
                        pitch = pitch,
                        playChimeBefore = playChime,
                        volumeBoost = volumeBoost
                    )
                    viewModel.updateVoiceSettings(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Tuning")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SlateTextMuted)
            }
        }
    )
}
