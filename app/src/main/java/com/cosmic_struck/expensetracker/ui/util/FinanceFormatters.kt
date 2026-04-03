package com.cosmic_struck.expensetracker.ui.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.asCurrency(): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(this)
}

fun Long.asMonthDay(): String {
    return DateTimeFormatter.ofPattern("MMM dd", Locale.US).format(
        Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    )
}

fun Long.asLongDate(): String {
    return DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US).format(
        Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    )
}
