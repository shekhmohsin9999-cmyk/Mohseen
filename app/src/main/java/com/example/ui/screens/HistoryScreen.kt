package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentTransaction
import com.example.model.UpiApp
import com.example.ui.MainViewModel
import com.example.ui.components.PaymentTransactionCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SuccessGreenDark
import com.example.ui.theme.SuccessGreenLight
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()
    val selectedAppFilter by viewModel.historyAppFilter.collectAsState()
    val selectedDateFilter by viewModel.historyDateFilter.collectAsState()

    var selectedTransactionForDetail by remember { mutableStateOf<PaymentTransaction?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    val totalFilteredAmount = filteredTransactions.sumOf { it.amount }
    val formattedTotal = if (totalFilteredAmount % 1.0 == 0.0) {
        String.format(Locale.US, "%,d", totalFilteredAmount.toLong())
    } else {
        String.format(Locale.US, "%,.2f", totalFilteredAmount)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Header with Export Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Payment History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "All voice-alerted merchant settlements",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavySecondary.copy(alpha = 0.1f))
                    .clickable { showExportDialog = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("export_statement_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.FileDownload,
                        contentDescription = "Export Statement",
                        tint = NavySecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Report",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search customer name or UPI ref ID...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = SlateTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = NavySecondary,
                unfocusedBorderColor = SlateBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Date Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Today", "Yesterday", "Last 7 Days", "All Time").forEach { dateLabel ->
                val isSelected = selectedDateFilter == dateLabel
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setDateFilter(dateLabel) },
                    label = {
                        Text(
                            text = dateLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = SlateTextDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // App Filter Chips (All, PhonePe, GPay, Paytm, BHIM)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All Chip
            FilterChip(
                selected = selectedAppFilter == null,
                onClick = { viewModel.setAppFilter(null) },
                label = { Text("All Apps", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NavySecondary,
                    selectedLabelColor = Color.White
                )
            )

            listOf(UpiApp.PHONEPE, UpiApp.GPAY, UpiApp.PAYTM, UpiApp.BHIM).forEach { app ->
                val isSelected = selectedAppFilter == app
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setAppFilter(if (isSelected) null else app) },
                    label = { Text(app.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = app.brandColor,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ledger Summary Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filteredTransactions.size} Transactions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "Total: ₹$formattedTotal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SuccessGreenDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transactions List
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ReceiptLong,
                        contentDescription = "No data",
                        tint = SlateTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No matching payments found",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )
                    Text(
                        text = "Try clearing filters or search query",
                        fontSize = 12.sp,
                        color = SlateTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { txn ->
                    PaymentTransactionCard(
                        transaction = txn,
                        onSpeakClick = { viewModel.repeatPayment(txn) },
                        onClick = { selectedTransactionForDetail = txn }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }

    // Detail Dialog
    selectedTransactionForDetail?.let { txn ->
        TransactionDetailDialog(
            transaction = txn,
            onDismiss = { selectedTransactionForDetail = null },
            onSpeak = { viewModel.repeatPayment(txn) }
        )
    }

    // Export Statement Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "Merchant Statement Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Filter: $selectedDateFilter • ${filteredTransactions.size} Payments",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTextDark
                    )
                    Text(
                        text = "Total Settlement Amount: ₹$formattedTotal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreenDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All settlements are directly credited to your linked UPI bank account without merchant commission.",
                        fontSize = 12.sp,
                        color = SlateTextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Done")
                }
            }
        )
    }
}
