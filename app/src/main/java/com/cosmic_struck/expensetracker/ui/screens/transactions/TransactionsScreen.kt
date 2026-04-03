package com.cosmic_struck.expensetracker.ui.screens.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cosmic_struck.expensetracker.ui.components.EmptyTransactionsState
import com.cosmic_struck.expensetracker.ui.components.FinanceFloatingActionButton
import com.cosmic_struck.expensetracker.ui.components.SectionHeader
import com.cosmic_struck.expensetracker.ui.components.SwipeDeleteBackground
import com.cosmic_struck.expensetracker.ui.components.TransactionItem
import com.cosmic_struck.expensetracker.ui.viewmodel.FinanceDashboardViewModel

@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    onAddTransactionClick: () -> Unit = {},
    onEditTransactionClick: (String) -> Unit = {},
    viewModel: FinanceDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    pendingDeleteId?.let { transactionId ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete transaction") },
            text = { Text("This transaction will be removed permanently.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(transactionId)
                        pendingDeleteId = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FinanceFloatingActionButton(onClick = onAddTransactionClick)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                SectionHeader(
                    title = "All Activity",
                    action = "${uiState.transactions.size} items"
                )
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    EmptyTransactionsState(
                        title = "Nothing to show yet",
                        subtitle = "Add your first transaction to start building insights."
                    )
                }
            } else {
                items(
                    items = uiState.transactions,
                    key = { it.id }
                ) { transaction ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                pendingDeleteId = transaction.id
                            }
                            false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            SwipeDeleteBackground(modifier = Modifier.fillMaxWidth())
                        }
                    ) {
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onEditTransactionClick(transaction.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
