package com.example.usfitness.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.usfitness.database.payment.Payment
import com.example.usfitness.database.record.DebtInfo
import com.example.usfitness.ui.theme.getColor
import com.example.usfitness.viewmodel.PaymentSettlementViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSettlementScreen(navController: NavController, cid: Int) {

    val paymentSettlementViewModel: PaymentSettlementViewModel = hiltViewModel()

    val customerName = paymentSettlementViewModel.getCustomerName(cid).observeAsState()

    val currentDebt = paymentSettlementViewModel.getCurrentDebt(cid).observeAsState()

    Scaffold(topBar = {
        TopAppBar(title = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CurrentStatusCard(customerName.value, currentDebt.value)

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            if (currentDebt.value != null && currentDebt.value!! > 0)
                SettlePayment(cid)
            else
                Text(modifier = Modifier.padding(20.dp), text = "Customer is all settled ✅")
        }
    }
}

@Composable
fun CurrentStatusCard(customerName: String?, currentDebt: Int?) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                text = customerName ?: "Loading.."
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, text = "Current Debt")
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    color = if (currentDebt == 0) getColor("Green") else getColor("Red"),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    text = "$currentDebt ₹"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlePayment(cid: Int) {

    val context = LocalContext.current

    val paymentSettlementViewModel: PaymentSettlementViewModel = hiltViewModel()

    val debtInfos = paymentSettlementViewModel.getAllRecordWithDebt(cid).observeAsState()

    val selectedRecord = remember {
        mutableStateOf(DebtInfo(0, LocalDate.now(), 0))
    }

    val paymentAmount = remember {
        mutableStateOf("")
    }

    val expanded = remember {
        mutableStateOf(false)
    }

    val settlePayment = {
        val payment = Payment(
            null,
            rid = selectedRecord.value.rid,
            amount = Integer.valueOf(paymentAmount.value),
            date = LocalDate.now()
        )
        paymentSettlementViewModel.addPayment(context, payment)
        paymentAmount.value = "0"
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded.value,
            onExpandedChange = {
                expanded.value = !expanded.value
            }
        ) {
            OutlinedTextField(
                textStyle = TextStyle(textAlign = TextAlign.Start),
                singleLine = true,
                isError = false,
                value = if (selectedRecord.value.debt == 0) "" else "Expiry At ${
                    selectedRecord.value.endDate.format(
                        DateTimeFormatter.ofPattern("dd MMM yy")
                    )
                },  ${selectedRecord.value.debt} ₹",
                placeholder = {
                    Text(
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = "Package"
                    )
                },
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                modifier = Modifier
                    .menuAnchor()
                    .wrapContentSize(Alignment.CenterStart)
                    .fillMaxWidth()
            )

            DropdownMenu(
                modifier = Modifier
                    .exposedDropdownSize(),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },

                ) {
                debtInfos.value?.let {
                    it.map { debtInfo ->
                        val value =
                            "Expiry At ${debtInfo.endDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))},  ${debtInfo.debt} ₹"
                        DropdownMenuItem(
                            text = {
                                Text(
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    text = value
                                )
                            },
                            onClick = {
                                if (debtInfo.debt == 0) Toast.makeText(
                                    context,
                                    "Select another package",
                                    Toast.LENGTH_SHORT
                                ).show()
                                else {
                                    expanded.value = false
                                    selectedRecord.value = debtInfo
                                    paymentAmount.value = (-debtInfo.debt).toString()
                                }
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
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = paymentAmount.value,
            onValueChange = { value -> paymentAmount.value = value },
            placeholder = { Text(text = "Payment Amount") })
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            onClick = settlePayment
        ) {
            Text(modifier = Modifier.padding(8.dp), text = "Settle")
        }
    }
}