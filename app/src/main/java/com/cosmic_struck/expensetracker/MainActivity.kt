package com.cosmic_struck.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cosmic_struck.expensetracker.ui.navigation.Screens
import com.cosmic_struck.expensetracker.ui.navigation.MainNavGraph
import com.cosmic_struck.expensetracker.ui.theme.ExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val navHostController = rememberNavController()
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val tabs = listOf(
                    BottomDestination(Screens.Home, "Home", Icons.Rounded.AccountBalanceWallet),
                    BottomDestination(Screens.Transactions, "Transactions", Icons.AutoMirrored.Rounded.ReceiptLong),
                    BottomDestination(Screens.Insights, "Insights", Icons.Rounded.Insights),
                    BottomDestination(Screens.Goals, "Goals", Icons.Rounded.Flag)
                )
                val showBottomBar = tabs.any { it.screen.route == currentRoute }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                tabs.forEach { tab ->
                                    NavigationBarItem(
                                        selected = currentRoute == tab.screen.route,
                                        onClick = {
                                            if (currentRoute != tab.screen.route) {
                                                navHostController.navigate(tab.screen.route) {
                                                    popUpTo(navHostController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    MainNavGraph(
                        navController = navHostController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

private data class BottomDestination(
    val screen: Screens,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
