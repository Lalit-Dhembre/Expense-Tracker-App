package com.cosmic_struck.expensetracker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cosmic_struck.expensetracker.ui.components.BalanceCard
import com.cosmic_struck.expensetracker.ui.components.ChartPlaceholder
import com.cosmic_struck.expensetracker.ui.components.EmptyTransactionsState
import com.cosmic_struck.expensetracker.ui.components.FinanceFloatingActionButton
import com.cosmic_struck.expensetracker.ui.components.GoalProgressCard
import com.cosmic_struck.expensetracker.ui.components.SectionHeader
import com.cosmic_struck.expensetracker.ui.components.SummaryCard
import com.cosmic_struck.expensetracker.ui.components.TransactionItem
import com.cosmic_struck.expensetracker.ui.util.asCurrency
import com.cosmic_struck.expensetracker.ui.viewmodel.FinanceDashboardViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAddTransactionClick: () -> Unit = {},
    onSeeAllTransactionsClick: () -> Unit = {},
    onManageGoalClick: () -> Unit = {},
    onEditTransactionClick: (String) -> Unit = {},
    viewModel: FinanceDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val recentTransactions = uiState.transactions.take(4)
    val primaryGoal = uiState.goals.firstOrNull()

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
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                BalanceCard(
                    totalBalance = uiState.balance.asCurrency(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                BoxWithConstraints {
                    val cardSpacing = if (maxWidth > 600.dp) 18.dp else 14.dp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(cardSpacing)
                    ) {
                        SummaryCard(
                            title = "Income",
                            amount = uiState.totalIncome.asCurrency(),
                            isIncome = true,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            title = "Expenses",
                            amount = uiState.totalExpense.asCurrency(),
                            isIncome = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                ChartPlaceholder(
                    weeklyTrends = uiState.weeklyTrends,
                    topCategoryLabel = uiState.topSpendingCategory,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                GoalProgressCard(
                    goal = primaryGoal,
                    onManageClick = onManageGoalClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                SectionHeader(
                    title = "Recent Transactions",
                    action = "See all",
                    onActionClick = onSeeAllTransactionsClick
                )
            }

            if (recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionsState(
                        title = "No transactions yet",
                        subtitle = "Your latest income and expenses will appear here."
                    )
                }
            } else {
                items(
                    items = recentTransactions,
                    key = { it.id }
                ) { transaction ->
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
