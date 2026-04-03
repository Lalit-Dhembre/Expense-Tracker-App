package com.cosmic_struck.expensetracker.ui.screens.add_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.cosmic_struck.expensetracker.domain.model.FinanceTransaction
import com.cosmic_struck.expensetracker.domain.model.TransactionType
import com.cosmic_struck.expensetracker.ui.util.asLongDate
import com.cosmic_struck.expensetracker.ui.viewmodel.FinanceDashboardViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: String? = null,
    onBackClick: () -> Unit,
    onTransactionSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinanceDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val existingTransaction = uiState.transactions.firstOrNull { it.id == transactionId }

    var amount by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf(TransactionType.Expense) }
    var category by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var hydratedForId by rememberSaveable { mutableStateOf<String?>(null) }

    val incomeCategories = listOf("Salary", "Freelance", "Bonus", "Interest")
    val expenseCategories = listOf("Groceries", "Dining", "Transport", "Shopping", "Bills", "Health")
    val categoryOptions = if (selectedType == TransactionType.Income) incomeCategories else expenseCategories
    val parsedAmount = amount.toDoubleOrNull()
    val isValid = parsedAmount != null && parsedAmount > 0.0 && category.isNotBlank()

    LaunchedEffect(existingTransaction?.id, transactionId) {
        if (transactionId != null && existingTransaction != null && hydratedForId != transactionId) {
            amount = existingTransaction.amount.toString()
            selectedType = existingTransaction.type
            category = existingTransaction.category
            note = existingTransaction.note.orEmpty()
            selectedDate = existingTransaction.date
            hydratedForId = transactionId
        }
    }

    if (showDatePicker) {
        TransactionDatePicker(
            initialDate = selectedDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { millis ->
                selectedDate = millis
                showDatePicker = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId == null) "Add Transaction" else "Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = if (transactionId == null) "Enter transaction details" else "Update transaction details",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TypeSelector(
                            selectedType = selectedType,
                            onTypeSelected = {
                                selectedType = it
                                category = ""
                            }
                        )

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { input ->
                                amount = input.filterIndexed { index, char ->
                                    char.isDigit() || (char == '.' && '.' !in input.take(index))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Amount") },
                            placeholder = { Text("0.00") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        CategoryPicker(
                            selectedCategory = category,
                            categories = categoryOptions,
                            onCategorySelected = { category = it }
                        )

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Category") },
                            placeholder = { Text("Choose or type a category") },
                            singleLine = true
                        )

                        DateInputField(
                            label = "Date",
                            value = selectedDate.asLongDate(),
                            onClick = { showDatePicker = true }
                        )

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Note") },
                            placeholder = { Text("Optional details") },
                            minLines = 3,
                            maxLines = 4
                        )

                        Button(
                            onClick = {
                                val transaction = FinanceTransaction(
                                    id = existingTransaction?.id ?: UUID.randomUUID().toString(),
                                    amount = parsedAmount ?: 0.0,
                                    type = selectedType,
                                    category = category.trim(),
                                    date = selectedDate,
                                    note = note.trim().ifBlank { null }
                                )
                                if (transactionId == null) {
                                    viewModel.saveTransaction(transaction)
                                } else {
                                    viewModel.updateTransaction(transaction)
                                }
                                onTransactionSaved()
                            },
                            enabled = isValid,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (transactionId == null) "Save Transaction" else "Update Transaction")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateInputField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
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
                    contentDescription = "Select $label",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun TypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Transaction Type",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(TransactionType.Expense, TransactionType.Income).forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(if (type == TransactionType.Income) "Income" else "Expense") }
                )
            }
        }
    }
}

@Composable
private fun CategoryPicker(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Quick Categories",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.forEach { item ->
                FilterChip(
                    selected = selectedCategory.equals(item, ignoreCase = true),
                    onClick = { onCategorySelected(item) },
                    label = { Text(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDatePicker(
    initialDate: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis ?: initialDate)
                }
            ) {
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
