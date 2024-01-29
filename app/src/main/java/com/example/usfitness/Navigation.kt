package com.example.usfitness

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.usfitness.database.customer.CustomerRepository
import com.example.usfitness.database.USFitnessDatabase
import com.example.usfitness.database.record.RecordRepository
import com.example.usfitness.screens.AddCustomerScreen
import com.example.usfitness.screens.MainScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.AddCustomerScreen.route) {
            AddCustomerScreen(navController = navController)
        }
    }
}