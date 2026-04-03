package com.cosmic_struck.expensetracker.ui.navigation

sealed class Screens(val route: String) {
    data object Home : Screens("home")
    data object Transactions : Screens("transactions")
    data object Insights : Screens("insights")
    data object GoalManagement : Screens("goal_management")
    data object Goals : Screens("goals")
    data object AddTransaction : Screens("add_transaction")
    data object EditTransaction : Screens("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: String): String = "edit_transaction/$transactionId"
    }
}
