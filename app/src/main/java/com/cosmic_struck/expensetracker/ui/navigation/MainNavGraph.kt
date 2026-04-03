package com.cosmic_struck.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cosmic_struck.expensetracker.ui.screens.add_edit.AddTransactionScreen
import com.cosmic_struck.expensetracker.ui.screens.goals.GoalManagementScreen
import com.cosmic_struck.expensetracker.ui.screens.home.HomeScreen
import com.cosmic_struck.expensetracker.ui.screens.insights.InsightsScreen
import com.cosmic_struck.expensetracker.ui.screens.transactions.TransactionsScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        modifier = modifier
    ) {
        composable(route = Screens.Home.route) {
            HomeScreen(
                onAddTransactionClick = {
                    navController.navigate(Screens.AddTransaction.route)
                },
                onSeeAllTransactionsClick = {
                    navController.navigate(Screens.Transactions.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onManageGoalClick = {
                    navController.navigate(Screens.GoalManagement.route)
                },
                onEditTransactionClick = { transactionId ->
                    navController.navigate(Screens.EditTransaction.createRoute(transactionId))
                }
            )
        }
        composable(route = Screens.Transactions.route) {
            TransactionsScreen(
                onAddTransactionClick = {
                    navController.navigate(Screens.AddTransaction.route)
                },
                onEditTransactionClick = { transactionId ->
                    navController.navigate(Screens.EditTransaction.createRoute(transactionId))
                }
            )
        }
        composable(route = Screens.Insights.route) {
            InsightsScreen()
        }
        composable(route = Screens.Goals.route) {
            GoalManagementScreen(
                onBackClick = { navController.navigateUp() },
                showBackButton = false
            )
        }
        composable(route = Screens.GoalManagement.route) {
            GoalManagementScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Screens.AddTransaction.route) {
            AddTransactionScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionSaved = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screens.EditTransaction.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddTransactionScreen(
                transactionId = backStackEntry.arguments?.getString("transactionId"),
                onBackClick = { navController.popBackStack() },
                onTransactionSaved = { navController.popBackStack() }
            )
        }
    }
}
