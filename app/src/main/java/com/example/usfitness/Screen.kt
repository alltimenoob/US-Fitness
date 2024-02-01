package com.example.usfitness

sealed class Screen(val route : String){
    object MainScreen : Screen("main_screen")
    object AddCustomerScreen : Screen("add_customer_screen")
    object PaymentSettlementScreen : Screen("payment_settlement_screen/{cid}")
    object ExtendPackageScreen : Screen("extend_package_screen/{cid}")
    object CustomerDetailsScreen : Screen("customer_details_screen/{cid}")
}