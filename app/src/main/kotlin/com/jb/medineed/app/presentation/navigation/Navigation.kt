package com.jb.medineed.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jb.medineed.app.presentation.page.entry.MedicineEntryScreen
import com.jb.medineed.app.presentation.page.lowstock.LowStockScreen
import com.jb.medineed.app.presentation.page.outofstock.OutOfStockScreen
import com.jb.medineed.app.presentation.page.reports.ReportsScreen
import com.jb.medineed.app.presentation.page.stock.StockListScreen
import com.jb.medineed.app.presentation.page.update.StockUpdateScreen

sealed class Screen(val route: String) {
    object StockList : Screen("stock_list")
    object MedicineEntry : Screen("medicine_entry")
    object EditMedicine : Screen("edit_medicine/{medicineId}") {
        fun createRoute(id: Long) = "edit_medicine/$id"
    }
    object LowStock : Screen("low_stock")
    object OutOfStock : Screen("out_of_stock")
    object Reports : Screen("reports")
    object StockUpdate : Screen("stock_update/{medicineId}") {
        fun createRoute(id: Long) = "stock_update/$id"
    }
}

@Composable
fun MediStockNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.StockList.route
    ) {
        composable(Screen.StockList.route) {
            StockListScreen(
                onAddMedicine = { navController.navigate(Screen.MedicineEntry.route) },
                onMedicineClick = { id -> navController.navigate(Screen.StockUpdate.createRoute(id)) },
                onEditClick = { id -> navController.navigate(Screen.EditMedicine.createRoute(id)) }
            )
        }

        composable(Screen.MedicineEntry.route) {
            MedicineEntryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditMedicine.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("medicineId") ?: return@composable
            MedicineEntryScreen(
                medicineId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LowStock.route) {
            LowStockScreen(
                onNavigateBack = { navController.popBackStack() },
                onMedicineClick = { id -> navController.navigate(Screen.StockUpdate.createRoute(id)) }
            )
        }

        composable(Screen.OutOfStock.route) {
            OutOfStockScreen(
                onNavigateBack = { navController.popBackStack() },
                onMedicineClick = { id -> navController.navigate(Screen.StockUpdate.createRoute(id)) }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.StockUpdate.route,
            arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("medicineId") ?: return@composable
            StockUpdateScreen(
                medicineId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}