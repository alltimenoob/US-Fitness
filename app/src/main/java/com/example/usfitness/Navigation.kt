package com.example.usfitness

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.usfitness.screens.CustomerDetailsScreen
import com.example.usfitness.screens.CustomerFormScreen
import com.example.usfitness.screens.ExtendPackageScreen
import com.example.usfitness.screens.MainScreen
import com.example.usfitness.screens.PaymentSettlementScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.AddCustomerScreen.route) {
            CustomerFormScreen(navController = navController)
        }
        composable(route = Screen.PaymentSettlementScreen.route + "",arguments = listOf(navArgument("cid") { type = NavType.IntType })){
            it.arguments?.let { cid ->
                PaymentSettlementScreen(navController = navController,
                    cid.getInt("cid"))
            }
        }
        composable(route = Screen.ExtendPackageScreen.route + "",arguments = listOf(navArgument("cid") { type = NavType.IntType })){
            it.arguments?.let { cid ->
                ExtendPackageScreen(navController = navController,
                    cid.getInt("cid"))
            }
        }
        composable(route = Screen.CustomerDetailsScreen.route + "",arguments = listOf(navArgument("cid") { type = NavType.IntType })){
            it.arguments?.let { cid ->
                CustomerDetailsScreen(navController = navController,
                    cid.getInt("cid"))
            }
        }
    }
}