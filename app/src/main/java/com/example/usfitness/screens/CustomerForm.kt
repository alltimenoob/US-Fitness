package com.example.usfitness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.usfitness.Screen
import com.example.usfitness.viewmodel.CustomerFormViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(navController: NavController) {
    val calendarState = rememberUseCaseState()
    val context = LocalContext.current
    val customerFormViewModel : CustomerFormViewModel = hiltViewModel()
    val expanded = remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = {
                IconButton(onClick = { navController.navigate(Screen.MainScreen.route) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            })
        },

        ) { innerPadding ->
        Column {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = customerFormViewModel.cid.second,
                    modifier = Modifier.fillMaxWidth(),
                    value = customerFormViewModel.cid.first,
                    onValueChange = { value ->
                        customerFormViewModel.updateCustomer(
                            cid = Pair(
                                value,
                                false
                            )
                        )
                    },
                    label = {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = "Registration Number"
                        )
                    })

                OutlinedTextField(
                    singleLine = true,
                    isError = customerFormViewModel.firstName.second,
                    modifier = Modifier.fillMaxWidth(),
                    value = customerFormViewModel.firstName.first,
                    onValueChange = { value ->
                        customerFormViewModel.updateCustomer(
                            firstName = Pair(
                                value,
                                false
                            )
                        )
                    },
                    label = { Text("First Name") })

                OutlinedTextField(
                    singleLine = true,
                    isError = customerFormViewModel.lastName.second,
                    modifier = Modifier.fillMaxWidth(),
                    value = customerFormViewModel.lastName.first,
                    onValueChange = { value ->
                        customerFormViewModel.updateCustomer(
                            lastName = Pair(
                                value,
                                false
                            )
                        )
                    },
                    label = { Text("Last Name") })

                OutlinedTextField(
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = customerFormViewModel.mobile.second,
                    value = customerFormViewModel.mobile.first,
                    onValueChange = { value ->
                        customerFormViewModel.updateCustomer(
                            mobile = Pair(
                                value,
                                false
                            )
                        )
                    },
                    label = { Text("Mobile") })

                Row {

                    OutlinedTextField(
                        singleLine = true,
                        enabled = false,
                        isError = customerFormViewModel.joinDate.second,
                        value = customerFormViewModel.joinDate.first,
                        onValueChange = { /* Do Nothing */ },
                        modifier = Modifier
                            .clickable { calendarState.show() }
                            .weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        label = { Text("Join Date") })

                    Spacer(modifier = Modifier.size(10.dp))

                    CalendarDialog(
                        state = calendarState,
                        selection = CalendarSelection.Date { date ->
                            customerFormViewModel.updateCustomer(
                                joinDate = Pair(
                                    date.toString(),
                                    false
                                )
                            )
                        })


                    ExposedDropdownMenuBox(
                        modifier = Modifier.weight(1f),
                        expanded = expanded.value,
                        onExpandedChange = {
                            expanded.value = !expanded.value
                        }
                    ) {
                        OutlinedTextField(
                            textStyle = TextStyle(textAlign = TextAlign.Start),
                            singleLine = true,
                            isError = customerFormViewModel.gymPackage.second,
                            value = customerFormViewModel.gymPackage.first,
                            label = {
                                Text(
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    text = "Months"
                                )
                            },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                            modifier = Modifier
                                .menuAnchor()
                                .wrapContentSize(Alignment.CenterStart)
                        )

                        DropdownMenu(
                            modifier = Modifier
                                .exposedDropdownSize()
                                .wrapContentSize(),
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false },

                            ) {
                            listOf(1, 3, 6, 12).map { month ->
                                val value = if (month == 1) "$month Month" else "$month Months"
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            text = value
                                        )
                                    },
                                    onClick = {
                                        customerFormViewModel.updateCustomer(
                                            gymPackage = Pair(
                                                value,
                                                false
                                            )
                                        )
                                        expanded.value = false
                                    },
                                )
                                Spacer(
                                    modifier = Modifier
                                        .height(0.5.dp)
                                        .fillMaxWidth()
                                        .background(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }

                Row {
                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        value = customerFormViewModel.total.first,
                        isError = customerFormViewModel.total.second,
                        onValueChange = { value ->
                            if (value.isEmpty() || value.matches(Regex("^\\d+\$")))
                                customerFormViewModel.updateCustomer(
                                    total = Pair(value, false),
                                    paid = Pair(value, false)
                                )
                        },
                        label = { Text("Total") },
                        prefix = { Text("₹") })

                    Spacer(modifier = Modifier.size(10.dp))

                    OutlinedTextField(
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        value = customerFormViewModel.paid.first,
                        isError = customerFormViewModel.paid.second,
                        onValueChange = { value ->
                            customerFormViewModel.updateCustomer(
                                paid = Pair(
                                    value,
                                    false
                                )
                            )
                        },
                        label = { Text("Paid") },
                        prefix = { Text("₹") }
                    )

                }

                Spacer(modifier = Modifier.size(30.dp))

                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(5.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ), onClick = { customerFormViewModel.addCurrentCustomer(context) }) {
                    Text("Add Customer")
                }
            }
        }
    }

}