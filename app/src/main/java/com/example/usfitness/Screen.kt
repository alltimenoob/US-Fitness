package com.example.usfitness

sealed class Screen(val route : String){
    object MainScreen : Screen("main_screen")
    object AddCustomerScreen : Screen("add_customer_screen")
}