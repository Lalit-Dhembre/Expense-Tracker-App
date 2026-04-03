package com.cosmic_struck.expensetracker.ui.screens.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cosmic_struck.expensetracker.domain.model.CategoryExpense
import com.cosmic_struck.expensetracker.ui.components.ChartPlaceholder
import com.cosmic_struck.expensetracker.ui.util.asCurrency
import com.cosmic_struck.expensetracker.ui.viewmodel.FinanceDashboardViewModel
import kotlin.math.roundToInt

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: FinanceDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            ChartPlaceholder(
                weeklyTrends = uiState.weeklyTrends,
                topCategoryLabel = uiState.topSpendingCategory,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            IncomeExpenseComparisonCard(
                income = uiState.totalIncome,
                expense = uiState.totalExpense
            )
        }

        item {
            CategoryBreakdownCard(
                breakdown = uiState.categoryBreakdown
            )
        }

        item {
            ExpensePieChartCard(
                breakdown = uiState.categoryBreakdown
            )
        }

        item {
            SmartInsightCard(
                topSpendingCategory = uiState.topSpendingCategory,
                latestWeekTotal = uiState.weeklyTrends.lastOrNull()?.totalExpense
            )
        }
    }
}

@Composable
private fun ExpensePieChartCard(
    breakdown: List<CategoryExpense>
) {
    val slices = breakdown.take(5)
    val total = slices.sumOf { it.totalAmount }
    val palette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        Color(0xFF6C8EF5),
        Color(0xFFF4B860)
    )

    InsightCard(
        title = "Expense Share",
        subtitle = "A pie view of where your money is going."
    ) {
        if (slices.isEmpty() || total <= 0.0) {
            Text(
                text = "No expense data yet. Add spending to see the pie chart.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        var startAngle = -90f
                        slices.forEachIndexed { index, item ->
                            val sweepAngle = ((item.totalAmount / total) * 360f).toFloat()
                            drawArc(
                                color = palette[index % palette.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 46f)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    slices.forEachIndexed { index, item ->
                        val percentage = ((item.totalAmount / total) * 100).roundToInt()
                        InsightLegendRow(
                            color = palette[index % palette.size],
                            label = item.category,
                            value = "${item.totalAmount.asCurrency()} • $percentage%"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomeExpenseComparisonCard(
    income: Double,
    expense: Double
) {
    val total = (income + expense).takeIf { it > 0.0 } ?: 1.0
    val incomeRatio = (income / total).toFloat().coerceIn(0f, 1f)
    val expenseRatio = (expense / total).toFloat().coerceIn(0f, 1f)

    InsightCard(title = "Cash Flow Split", subtitle = "Compare what came in versus what went out.") {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            InsightBar(
                label = "Income",
                value = income.asCurrency(),
                progress = incomeRatio,
                color = MaterialTheme.colorScheme.tertiary
            )
            InsightBar(
                label = "Expenses",
                value = expense.asCurrency(),
                progress = expenseRatio,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun CategoryBreakdownCard(
    breakdown: List<CategoryExpense>
) {
    val topCategories = breakdown.take(4)
    val maxAmount = topCategories.maxOfOrNull { it.totalAmount }?.takeIf { it > 0.0 } ?: 1.0
    val palette = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        Color(0xFF6C8EF5)
    )

    InsightCard(
        title = "Category Breakdown",
        subtitle = "Your biggest expense buckets right now."
    ) {
        if (topCategories.isEmpty()) {
            Text(
                text = "No expense categories yet. Add spending to see the breakdown.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                topCategories.forEachIndexed { index, item ->
                    InsightBar(
                        label = item.category,
                        value = item.totalAmount.asCurrency(),
                        progress = (item.totalAmount / maxAmount).toFloat().coerceIn(0f, 1f),
                        color = palette[index % palette.size]
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightLegendRow(
    color: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SmartInsightCard(
    topSpendingCategory: String?,
    latestWeekTotal: Double?
) {
    InsightCard(
        title = "Smart Insight",
        subtitle = "A quick summary from your recent activity."
    ) {
        Text(
            text = when {
                topSpendingCategory == null ->
                    "Add a few expenses to unlock personalized spending insights."
                latestWeekTotal == null ->
                    "Your top spending category is $topSpendingCategory."
                else ->
                    "Your top spending category is $topSpendingCategory, and your latest tracked week closed at ${latestWeekTotal.asCurrency()} in expenses."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InsightBar(
    label: String,
    value: String,
    progress: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceAtLeast(0.08f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Composable
private fun InsightCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}
