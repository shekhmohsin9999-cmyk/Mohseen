package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UiNotice
import com.example.ui.MainViewModel
import com.example.ui.components.PayAlertTopBar
import com.example.ui.screens.CustomVoiceDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LicenseProfileScreen
import com.example.ui.screens.NotificationSetupDialog
import com.example.ui.screens.UpiSettingsScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.theme.NavyDark
import com.example.ui.theme.PayAlertTheme
import com.example.ui.theme.SuccessGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PayAlertTheme {
                PayAlertApp()
            }
        }
    }
}

@Composable
fun PayAlertApp(viewModel: MainViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val merchantProfile by viewModel.merchantProfile.collectAsState()
    val licenseStatus by viewModel.licenseStatus.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val showSetupInstructions by viewModel.showSetupInstructions.collectAsState()
    val showCustomVoiceModal by viewModel.showCustomVoiceModal.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to repository notices (Success, Info, Error) and show Snackbar
    LaunchedEffect(Unit) {
        viewModel.uiNotice.collect { notice ->
            val message = when (notice) {
                is UiNotice.Success -> "✓ ${notice.message}"
                is UiNotice.Error -> "⚠ ${notice.message}"
                is UiNotice.Info -> "ℹ ${notice.message}"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    // BackHandler: returns to Dashboard if currently in subscreen
    BackHandler(enabled = currentTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pay_alert_main_scaffold"),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PayAlertTopBar(
                shopName = merchantProfile.shopName,
                isServiceActive = isServiceActive,
                isSpeaking = isSpeaking,
                isLicenseActive = licenseStatus.isActivated,
                onTestSoundClick = {
                    viewModel.testVoiceAnnouncement()
                },
                onShopNameClick = {
                    viewModel.selectTab(4)
                },
                onStatusBadgeClick = {
                    viewModel.openSetupInstructions()
                },
                onLicenseBadgeClick = {
                    viewModel.selectTab(4)
                },
                onLogoClick = {
                    viewModel.playChime()
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyDark,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                val navItems = listOf(
                    NavItem(
                        title = "Dashboard",
                        selectedIcon = Icons.Filled.Dashboard,
                        unselectedIcon = Icons.Outlined.Dashboard,
                        tabIndex = 0
                    ),
                    NavItem(
                        title = "History",
                        selectedIcon = Icons.Filled.ReceiptLong,
                        unselectedIcon = Icons.Outlined.ReceiptLong,
                        tabIndex = 1
                    ),
                    NavItem(
                        title = "Voice",
                        selectedIcon = Icons.Filled.RecordVoiceOver,
                        unselectedIcon = Icons.Outlined.RecordVoiceOver,
                        tabIndex = 2
                    ),
                    NavItem(
                        title = "UPI Alerts",
                        selectedIcon = Icons.Filled.NotificationsActive,
                        unselectedIcon = Icons.Outlined.Notifications,
                        tabIndex = 3
                    ),
                    NavItem(
                        title = "License",
                        selectedIcon = Icons.Filled.Security,
                        unselectedIcon = Icons.Outlined.Security,
                        tabIndex = 4
                    )
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tabIndex
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tabIndex) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDark,
                            selectedTextColor = SuccessGreen,
                            indicatorColor = SuccessGreen,
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_${item.tabIndex}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { viewModel.selectTab(1) },
                    onNavigateToVoiceSettings = { viewModel.selectTab(2) },
                    onNavigateToUpiSettings = { viewModel.selectTab(3) }
                )
                1 -> HistoryScreen(
                    viewModel = viewModel
                )
                2 -> VoiceSettingsScreen(
                    viewModel = viewModel
                )
                3 -> UpiSettingsScreen(
                    viewModel = viewModel
                )
                4 -> LicenseProfileScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // App-wide Setup Notification Listener Instructions Dialog (Req 8)
    if (showSetupInstructions) {
        NotificationSetupDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeSetupInstructions() }
        )
    }

    // App-wide Custom Voice Tuning Dialog (Req 7)
    if (showCustomVoiceModal) {
        CustomVoiceDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeCustomVoiceModal() }
        )
    }
}

private data class NavItem(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tabIndex: Int
)
