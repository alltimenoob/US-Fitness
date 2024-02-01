package com.example.usfitness.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.Layout
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usfitness.Screen
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.usfitness.R
import com.example.usfitness.database.customer.CustomizedCustomer
import com.example.usfitness.database.payment.Payment
import com.example.usfitness.ui.theme.getColor
import com.example.usfitness.viewmodel.MainScreenViewModel
import com.example.usfitness.viewmodel.Sort
import com.example.usfitness.viewmodel.SortMenu
import com.example.usfitness.viewmodel.SortState
import com.example.usfitness.viewmodel.helper.FilterMenu
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.shape.chartShape
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.component.text.VerticalPosition
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.composed.plus
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("WeekBasedYear")
@Composable
fun MainScreen(navController: NavController) {

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()
    val customers = mainScreenViewModel.getAllByExpiryDate().observeAsState()

    Scaffold(
        topBar = { Title() },
        floatingActionButton = {
            AddCustomerButton {
                navController.navigate(Screen.AddCustomerScreen.route)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {

            SearchAndFilterCustomer()

            customers.value?.let {
                LazyColumn(
                    verticalArrangement = Arrangement.Center
                ) {
                    items(it) { customer ->
                        CustomerListItem(customer, navController)
                    }
                }
            }

            if (customers.value?.isEmpty() == true) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "No Records To Show")
                }
            }


        }

    }
}


// Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListItem(customer: CustomizedCustomer, navController: NavController) {

    val context = LocalContext.current

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    val customerPaymentRecords =
        mainScreenViewModel.getPaymentsForCustomer(customer.cid).observeAsState()

    val (customerCard, setCustomerCard) = remember {
        mutableStateOf(false)
    }

    val customerCardSheetState = rememberModalBottomSheetState()

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .background(
            color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(5.dp)
        )
        .clickable {
            setCustomerCard(true)
        }) {
        Text(
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(10.dp),
            textAlign = TextAlign.Center,
            text = "${customer.cid}"
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 20.dp, horizontal = 10.dp)
                .width(1.dp)
                .background(MaterialTheme.colorScheme.onBackground)
        )
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight()
        ) {
            Text(
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                text = customer.firstName + " " + customer.lastName
            )
            CustomerDebt(customer.debt)
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 20.dp, horizontal = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .shadow(elevation = 3.dp, shape = CircleShape)
                    .background(mainScreenViewModel.getExpiryTintForCustomer(customer.endDate))
            )
            Text(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                text = customer.endDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))
            )
        }

    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    )

    val customerPaymentRecordList = (customerPaymentRecords.value ?: listOf(
        Payment(0, 0, 0,  LocalDate.now()))
    )

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yy")

    val paidData = customerPaymentRecordList.mapIndexed { index, it ->
        "${it.date.format(dateTimeFormatter)} $index" to  it.amount
    }.associate { it.first to it.second }


    if (customerCard) ModalBottomSheet(sheetState = customerCardSheetState,
        onDismissRequest = { setCustomerCard(false) }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column {

                CustomerChart(paidData = paidData)

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    text = "History"
                )

                Spacer(modifier = Modifier.height(10.dp))


                listOf(
                    Pair("Call ${customer.firstName}") {
                        val uri = Uri.parse("tel:" + customer.mobile)
                        val intent = Intent(Intent.ACTION_DIAL, uri)
                        context.startActivity(intent)
                    },
                    Pair("Settle Payment") { navController.navigate("payment_settlement_screen/${customer.cid}") },
                    Pair("Extend Package") { navController.navigate("extend_package_screen/${customer.cid}") },
                    Pair("Customer Details") { navController.navigate("customer_details_screen/${customer.cid}") }
                ).map {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = it.second
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
                            textAlign = TextAlign.Start,
                            text = it.first,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
    }
}

@Composable
fun CustomerChart( paidData: Map<String, Int>) {
    var iterator = 0f

    val paidDataXValuesToDates =
        paidData.keys.associateBy { iterator ++ }
    val paidDataEntryModel =
        entryModelOf(paidDataXValuesToDates.keys.zip(paidData.values, ::entryOf))

    val barTextComponentBuilder = TextComponent.Builder()
    barTextComponentBuilder.padding = MutableDimensions(10f, 10f)
    barTextComponentBuilder.textAlignment = Layout.Alignment.ALIGN_CENTER
    barTextComponentBuilder.color = MaterialTheme.colorScheme.secondary.toArgb()

    val verticalAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
        "${value.toInt()} ₹"
    }

    val paidDataColumnComponent = listOf(
        LineComponent(
            color = MaterialTheme.colorScheme.primaryContainer.toArgb(),
            thicknessDp = 25f,
            shape = RoundedCornerShape(5.dp).chartShape()
        ),
    )

    val paidChart = columnChart(
        spacing = 10.dp,
        columns = paidDataColumnComponent,
        dataLabel = barTextComponentBuilder.build(),
        dataLabelVerticalPosition = VerticalPosition.Top,
        dataLabelRotationDegrees = 270f,
    )

    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            ((paidDataXValuesToDates)[value] ?: ""
            ).dropLast(2)
        }

    val dateTextComponentBuilder = TextComponent.Builder()
    dateTextComponentBuilder.color = currentChartStyle.axis.axisLabelColor.toArgb()
    dateTextComponentBuilder.padding = MutableDimensions(2f, 2f)
    dateTextComponentBuilder.lineCount = 3

    Chart(
        getXStep = { 1f },
        modifier = Modifier.padding(horizontal = 20.dp),
        chart = paidChart,
        model = paidDataEntryModel,
        horizontalLayout = HorizontalLayout.FullWidth(
            scalableStartPaddingDp = 2f,
            scalableEndPaddingDp = 2f,
        ),
        startAxis = rememberStartAxis(valueFormatter = verticalAxisValueFormatter,
            itemPlacer = remember {
                AxisItemPlacer.Vertical.default(maxItemCount = 5)
            }),
        bottomAxis = rememberBottomAxis(
            label = dateTextComponentBuilder.build(),
            valueFormatter = horizontalAxisValueFormatter,
            itemPlacer = remember {
                AxisItemPlacer.Horizontal.default(shiftExtremeTicks = false)
            },
            tickLength = 0.dp
        ),
        autoScaleUp = AutoScaleUp.None
    )
}

@Composable
fun CustomerDebt(debt: String) {
    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        val isPaid = debt[0] != '-'
        if (!isPaid) Image(
            modifier = Modifier.fillMaxHeight(0.5f),
            painter = painterResource(id = R.drawable.baseline_arrow_downward_24),
            contentDescription = "Down"
        )
        Text(
            fontWeight = FontWeight.SemiBold,
            fontSize = LocalTextStyle.current.fontSize.times(0.8f),
            color = if (!isPaid) getColor("Red") else getColor("Green"),
            textAlign = TextAlign.Center,
            text = if (isPaid) "Paid" else "$debt ₹"
        )
    }
}

@Composable
fun BackupAndRestoreButton() {

    val context = LocalContext.current

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    val databaseFile = mainScreenViewModel.getDatabaseFile().observeAsState()

    val (showBackupRestoreSelector, setShowBackupRestoreSelector) = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(databaseFile.value) {
        if (databaseFile.value != null) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra(
                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        context, "com.example.usfitness.provider", databaseFile.value!!
                    )
                )
                type = "application/zip"
            }

            val shareIntent = Intent.createChooser(sendIntent, "Save Database")
            context.startActivity(shareIntent)
            setShowBackupRestoreSelector(false)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        val tempZipFile = File(context.filesDir, "temp.zip")
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)

            FileOutputStream(tempZipFile).use {
                inputStream?.copyTo(it, 1024) ?: ""
            }

            inputStream?.close()
            mainScreenViewModel.restoreToDatabase(context.filesDir, tempZipFile)

            setShowBackupRestoreSelector(false)
            tempZipFile.delete()
        }
    }

    val onDismissRequest = { setShowBackupRestoreSelector(false) }
    IconButton(modifier = Modifier.padding(0.dp),
        onClick = { setShowBackupRestoreSelector(true) }) {
        Icon(painterResource(id = R.drawable.baseline_restore_24), contentDescription = "Save")
    }

    if (showBackupRestoreSelector) {
        Dialog(onDismissRequest = onDismissRequest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        modifier = Modifier.padding(20.dp),
                        text = "Backup Settings",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        overflow = TextOverflow.Ellipsis,
                        fontSize = LocalTextStyle.current.fontSize.times(0.7f),
                        text = "Save your data in a file or restore it using saved backup file."
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        TextButton(
                            onClick = {
                                mainScreenViewModel.saveDatabaseToFile(context.filesDir)
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Backup")
                        }
                        TextButton(
                            onClick = {
                                launcher.launch("*/*")
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Title() {
    TopAppBar(title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                modifier = Modifier
                    .width(50.dp)
                    .clip(
                        CircleShape
                    ),
                contentScale = ContentScale.Fit,
                painter = painterResource(id = R.drawable.usfitness_logo),
                contentDescription = "Logo"
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "US FITNESS", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            BackupAndRestoreButton()
            Spacer(modifier = Modifier.size(10.dp))
        }
    })
}

@Composable
fun AddCustomerButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text("New") },
        icon = { Icon(Icons.Filled.Add, contentDescription = "") },
        onClick = onClick
    )
}


@Composable
fun SearchAndFilterCustomer() {

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    val (searchQuery, setSearchQuery) = remember {
        mutableStateOf("")
    }

    val onSearchQueryChange = { value: String ->
        setSearchQuery(value)
        mainScreenViewModel.searchCustomer(value)
    }

    TextField(
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(text = "Search", textAlign = TextAlign.Center) },
        shape = RoundedCornerShape(10.dp),
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search") },
        trailingIcon = { FilterCustomer() },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )

    Spacer(modifier = Modifier.size(10.dp))

}

@Composable
fun FilterCustomer() {

    val mainScreenViewModel: MainScreenViewModel = hiltViewModel()

    val (showFilterMenu, setShowFilterMenu) = remember {
        mutableStateOf(false)
    }

    val filterMenuList = remember {
        mutableStateListOf(
            FilterMenu("Active", getColor("Green"), true),
            FilterMenu("Close To Expiry", getColor("Yellow"), true),
            FilterMenu("Expired", getColor("Red"), false)
        )
    }

    var selectedSort by remember {
        mutableStateOf(SortMenu(current = Sort.ByExpiryDate, state = SortState.Ascending))
    }

    IconButton(onClick = { setShowFilterMenu(true) }) {
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Filter Menu")
    }

    val onFilterMenuItemClick: (index: Int) -> () -> Unit = { index ->
        {
            filterMenuList.set(
                index = index,
                filterMenuList[index].copy(selected = !filterMenuList.get(index = index).selected)
            )
            mainScreenViewModel.filterCustomer(filterMenuList)
            setShowFilterMenu(false)
        }
    }

    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { setShowFilterMenu(false) }) {
        filterMenuList.forEachIndexed { index, filterMenu ->
            DropdownMenuItem(trailingIcon = {
                if (filterMenu.selected) Icon(
                    imageVector = Icons.Filled.Check, contentDescription = "Check"
                )
            }, text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .shadow(elevation = 3.dp, shape = CircleShape)
                            .background(filterMenu.color)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = filterMenu.name
                    )
                }
            }, onClick = onFilterMenuItemClick(index)
            )
        }
        Divider()


        listOf(Sort.ByName, Sort.ByRegistration, Sort.ByExpiryDate).map { sort ->
            DropdownMenuItem(text = { Text(text = sort.text) }, trailingIcon = {
                if (selectedSort.current == sort) Icon(
                    imageVector = if (selectedSort.state == SortState.Ascending) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Check"
                )
            }, onClick = {
                selectedSort = if (selectedSort.current == sort) {
                    val state = when (selectedSort.state) {
                        SortState.Ascending -> SortState.Descending
                        else -> SortState.Ascending
                    }
                    SortMenu(sort, state)
                } else {
                    SortMenu(sort, SortState.Ascending)
                }

                mainScreenViewModel.sortCustomer(
                    selectedSort
                )
                setShowFilterMenu(false)
            })
        }
    }
}


