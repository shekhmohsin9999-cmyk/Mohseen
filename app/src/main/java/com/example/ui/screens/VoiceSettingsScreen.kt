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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.components.SoundWaveBars
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

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
                    text = "Voice Alert Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "Customize soundbox language, volume, and speech tone",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
            }
        }

        // ==============================================================
        // 1. LIVE VOICE TESTER CARD
        // ==============================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_tester_card"),
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
                                    .background(SuccessGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = "Audio Wave",
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Current Voice Preview",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${voiceSettings.language.displayName} (${voiceSettings.language.nativeName})",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        if (isSpeaking) {
                            SoundWaveBars(isAnimating = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sample speech text preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavyCard)
                            .border(1.dp, NavyBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = when (voiceSettings.language) {
                                VoiceLanguage.HINDI -> "फोनपे पर राहुल शर्मा से ₹500 प्राप्त हुए।"
                                VoiceLanguage.ENGLISH -> "Received 500 Rupees on PhonePe from Rahul Sharma."
                                VoiceLanguage.HINGLISH -> "PhonePe par Rahul Sharma se 500 Rupees praapt hue."
                                VoiceLanguage.MARATHI -> "फोनपे वर राहुल शर्मा कडून ₹500 प्राप्त झाले."
                                VoiceLanguage.TAMIL -> "போன்பே மூலம் ராகுல் சர்மாவிடமிருந்து ₹500 பெறப்பட்டது."
                                VoiceLanguage.TELUGU -> "ఫోన్‌పే ద్వారా రాహుల్ శర్మ నుండి ₹500 అందుకున్నారు."
                                VoiceLanguage.GUJARATI -> "ફોનપે પર રાહુલ શર્મા તરફથી ₹500 મળ્યા."
                                VoiceLanguage.BENGALI -> "ফোনপেতে রাহুল শর্মার কাছ থেকে ₹500 পাওয়া গেছে।"
                                VoiceLanguage.KANNADA -> "ಫೋನ್‍ಪೇ ಮೂಲಕ ರಾಹುಲ್ ಶರ್ಮಾ ಅವರಿಂದ ₹500 ಸ್ವೀಕರಿಸಲಾಗಿದೆ."
                                VoiceLanguage.PUNJABI -> "ਫ਼ੋਨਪੇ 'ਤੇ ਰਾਹੁਲ ਸ਼ਰਮਾ ਤੋਂ ₹500 ਪ੍ਰਾਪਤ ਹੋਏ।"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.testVoiceAnnouncement() },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreenDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_voice_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Test Voice",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (isSpeaking) {
                            OutlinedButton(
                                onClick = { viewModel.stopSpeaking() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(0.6f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Stop,
                                    contentDescription = "Stop",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stop", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.openCustomVoiceModal() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_screen_custom_tune_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Custom Voice Customization & Test", color = Color.White)
                    }
                }
            }
        }

        // ==============================================================
        // 2. LANGUAGE SELECTOR (10 INDIAN LANGUAGES)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = "Language",
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Soundbox Language",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark
                        )
                    }

                    Text(
                        text = "Choose your shop's regional announcement language:",
                        fontSize = 12.sp,
                        color = SlateTextMuted,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VoiceLanguage.values().forEach { lang ->
                            val isSelected = voiceSettings.language == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) NavyPrimary else Color(0xFFF8FAFC))
                                    .border(
                                        1.dp,
                                        if (isSelected) NavyPrimary else SlateBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        viewModel.updateVoiceSettings(voiceSettings.copy(language = lang))
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("lang_chip_${lang.name}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "${lang.displayName} • ${lang.nativeName}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else SlateTextDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==============================================================
        // 3. AUDIO TUNING: SPEED & PITCH
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
                        text = "Speech Rate & Tone",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speech Rate Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Speed", fontSize = 13.sp, color = SlateTextDark, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", voiceSettings.speechRate)}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    }

                    Slider(
                        value = voiceSettings.speechRate,
                        onValueChange = {
                            viewModel.updateVoiceSettings(voiceSettings.copy(speechRate = it))
                        },
                        valueRange = 0.7f..1.5f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = NavySecondary,
                            activeTrackColor = NavySecondary,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )

                    // Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetChip("0.9x Slow", isSelected = voiceSettings.speechRate == 0.9f) {
                            viewModel.updateVoiceSettings(voiceSettings.copy(speechRate = 0.9f))
                        }
                        PresetChip("1.0x Normal", isSelected = voiceSettings.speechRate == 1.0f) {
                            viewModel.updateVoiceSettings(voiceSettings.copy(speechRate = 1.0f))
                        }
                        PresetChip("1.2x Fast", isSelected = voiceSettings.speechRate == 1.2f) {
                            viewModel.updateVoiceSettings(voiceSettings.copy(speechRate = 1.2f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pitch Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pitch", fontSize = 13.sp, color = SlateTextDark, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.2f", voiceSettings.pitch)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    }

                    Slider(
                        value = voiceSettings.pitch,
                        onValueChange = {
                            viewModel.updateVoiceSettings(voiceSettings.copy(pitch = it))
                        },
                        valueRange = 0.7f..1.4f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = NavySecondary,
                            activeTrackColor = NavySecondary,
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }

        // ==============================================================
        // 4. SOUNDBOX AUDIO BOOST & CHIME TOGGLES
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
                        text = "Soundbox Audio Enhancement",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )

                    // Volume Boost Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Market Volume Auto-Boost",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "Automatically raises speaker volume to maximum for noisy bazaar environments",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                        Switch(
                            checked = voiceSettings.volumeBoost,
                            onCheckedChange = {
                                viewModel.updateVoiceSettings(voiceSettings.copy(volumeBoost = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreenDark
                            )
                        )
                    }

                    // Soundbox Chime Ding Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Soundbox Chime Beep",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "Plays electronic two-tone chime before the voice speaks",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                        Switch(
                            checked = voiceSettings.playChimeBefore,
                            onCheckedChange = {
                                viewModel.updateVoiceSettings(voiceSettings.copy(playChimeBefore = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreenDark
                            )
                        )
                    }

                    // Repeat Announcement Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Repeat Voice Alert Twice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "Announces payment a second time to prevent missed alerts",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                        Switch(
                            checked = voiceSettings.repeatTwice,
                            onCheckedChange = {
                                viewModel.updateVoiceSettings(voiceSettings.copy(repeatTwice = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreenDark
                            )
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
fun PresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) NavySecondary else Color(0xFFF1F5F9))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else SlateTextDark
        )
    }
}
