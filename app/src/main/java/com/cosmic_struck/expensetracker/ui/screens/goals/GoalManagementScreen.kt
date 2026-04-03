package com.cosmic_struck.expensetracker.ui.screens.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cosmic_struck.expensetracker.domain.model.FinancialGoal
import com.cosmic_struck.expensetracker.ui.util.asCurrency
import com.cosmic_struck.expensetracker.ui.util.asLongDate
import com.cosmic_struck.expensetracker.ui.viewmodel.FinanceDashboardViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalManagementScreen(
    onBackClick: () -> Unit,
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: FinanceDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val goal = uiState.goals.firstOrNull()

    var title by rememberSaveable { mutableStateOf("") }
    var targetAmount by rememberSaveable { mutableStateOf("") }
    var deadline by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var hydrated by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(goal?.id) {
        if (goal != null && !hydrated) {
            title = goal.title
            targetAmount = goal.targetAmount.toString()
            deadline = goal.deadline
            hydrated = true
        }
        if (goal == null) {
            hydrated = false
        }
    }

    if (showDatePicker) {
        GoalDatePicker(
            initialDate = deadline,
            onDismiss = { showDatePicker = false },
            onDateSelected = { millis ->
                deadline = millis
                showDatePicker = false
            }
        )
    }

    if (showDeleteDialog && goal != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete goal") },
            text = { Text("Your savings goal will be removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGoal(goal.id)
                        showDeleteDialog = false
                        title = ""
                        targetAmount = ""
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val parsedTarget = targetAmount.toDoubleOrNull()
    val isValid = title.isNotBlank() && parsedTarget != null && parsedTarget > 0.0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Savings Goal") },
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else {
                    {}
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Track a personal savings target",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (goal == null) {
                                "Set a target and we will compare it against your current balance."
                            } else {
                                "Current progress: ${goal.currentAmount.asCurrency()} of ${goal.targetAmount.asCurrency()}."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Goal name") },
                            placeholder = { Text("Emergency Fund") },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = targetAmount,
                            onValueChange = { input ->
                                targetAmount = input.filterIndexed { index, char ->
                                    char.isDigit() || (char == '.' && '.' !in input.take(index))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Target amount") },
                            placeholder = { Text("10000") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        GoalDateInputField(
                            value = deadline.asLongDate(),
                            onClick = { showDatePicker = true }
                        )

                        Button(
                            onClick = {
                                val updatedGoal = FinancialGoal(
                                    id = goal?.id ?: UUID.randomUUID().toString(),
                                    title = title.trim(),
                                    targetAmount = parsedTarget ?: 0.0,
                                    currentAmount = goal?.currentAmount ?: 0.0,
                                    deadline = deadline
                                )
                                if (goal == null) {
                                    viewModel.saveGoal(updatedGoal)
                                } else {
                                    viewModel.updateGoal(updatedGoal)
                                }
                                onBackClick()
                            },
                            enabled = isValid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (goal == null) "Create Goal" else "Update Goal")
                        }

                        if (goal != null) {
                            TextButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Delete Goal")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalDateInputField(
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Deadline",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = "Select deadline",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDatePicker(
    initialDate: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis ?: initialDate) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
