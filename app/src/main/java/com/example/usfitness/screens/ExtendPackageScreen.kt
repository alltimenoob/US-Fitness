package com.example.usfitness.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.usfitness.viewmodel.CustomerFormViewModel
import com.example.usfitness.viewmodel.ExtendPackageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtendPackageScreen(navController: NavController, cid : Int) {

    val context = LocalContext.current

    val extendPackageViewModel : ExtendPackageViewModel = hiltViewModel()

    extendPackageViewModel.loadJoinDate(cid)

    val onExtendPackage = {
        extendPackageViewModel.extendPackage(context)
    }

    Scaffold(topBar = { TopAppBar(title = { IconButton(onClick = { navController.popBackStack() }) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription =  "Back" )
    } }) }){
            innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(20.dp)) {
            CustomerPackage(customerFormViewModel = extendPackageViewModel)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = onExtendPackage
            ) {
                Text(modifier = Modifier.padding(8.dp), text = "Extend")
            }
        }
    }
}