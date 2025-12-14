package com.kontranik.easycycle.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultPhasesData.Companion.ar
import com.kontranik.easycycle.constants.DefaultSettings.Companion.defaultCycleLength
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.model.CDay
import com.kontranik.easycycle.ui.appbar.AppBar
import com.kontranik.easycycle.ui.appbar.AppBarAction
import com.kontranik.easycycle.ui.calendar.CalendarViewModel
import com.kontranik.easycycle.ui.shared.CustomDialog
import com.kontranik.easycycle.ui.shared.DatePickerModal
import com.kontranik.easycycle.ui.shared.NumberPicker
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall
import kotlinx.coroutines.launch
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navigateSettings: () -> Unit,
    calendarViewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val lastCycle by calendarViewModel.lastCycle.collectAsState(null)
    val cDays by calendarViewModel.cDays.collectAsState(emptyList())

    HomeContent(
        lastCycle = lastCycle,
        cDays = cDays,
        onOpenSettings = { coroutineScope.launch { navigateSettings() }},
        onSave = { date, length ->
            calendarViewModel.addCycle(date, length)
        }
    )
}

@Composable
fun HomeContent(
    lastCycle: Cycle?,
    cDays: List<CDay> = emptyList(),
    onSave: (Date, Int) -> Unit = {_, _ -> },
    onOpenSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.title_home,
                appBarActions = listOf{
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = Icons.Default.Settings,
                        description = R.string.settings,
                        onClick = { onOpenSettings() }
                    ))
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (lastCycle == null) {
                StartDataPicker(
                    onSave = onSave
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = paddingMedium)
                ) {
                    itemsIndexed(cDays) { index, cDay ->
                        CDayItem(
                            cday = cDay
                        )
                        if (index != cDays.size) Box( Modifier.height(paddingSmall))
                    }
                }
            }
        }
    }
}

@Composable
private fun StartDataPicker(
    onSave: (Date, Int) -> Unit = {_, _ -> }
) {

    var currentStartDate: Date? by remember() {
        mutableStateOf(null)
    }
    var currentAverageCycleLength by remember() {
        mutableIntStateOf(defaultCycleLength)
    }

    var showDatePicker by remember { mutableStateOf(false) }

    var showAverageLengthPicker by rememberSaveable() {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.padding(paddingMedium)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(paddingMedium)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.no_start_date_set),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = paddingMedium)
            )

            HorizontalDivider()

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = paddingMedium)
            ) {
                Text(
                    stringResource(R.string.start_date_of_last_cycle),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = {

                        showDatePicker = true
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = currentStartDate?.let{ CalendarViewModel.formatDateToString(it)}
                                ?: stringResource(R.string.no_date_set_date_placeholder),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    }
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingMedium)
            ) {
                Text(
                    stringResource(R.string.average_cycle_length),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = {
                        showAverageLengthPicker = true
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentAverageCycleLength == 0) stringResource(R.string.no_length_set_placeholder) else currentAverageCycleLength.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Numbers,
                            contentDescription = null
                        )
                    }
                }
                if (showAverageLengthPicker) {
                    CustomDialog(
                        title = stringResource(R.string.average_cycle_length),
                        onDismiss = { showAverageLengthPicker = false },
                    ) {
                        val averageLengthPickerListState =
                            rememberLazyListState(initialFirstVisibleItemIndex = currentAverageCycleLength - 1)
                        val values = remember {
                            (0..100).map { it.toString() }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NumberPicker(
                                list = values,
                                fontSize = 32.sp,
                                state = averageLengthPickerListState,
                                flingBehavior = rememberSnapFlingBehavior(lazyListState = averageLengthPickerListState)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    currentAverageCycleLength =
                                        averageLengthPickerListState.firstVisibleItemIndex + 1
                                    showAverageLengthPicker = false
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.save)
                                )
                            }
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    enabled = (currentAverageCycleLength > 0) && currentStartDate != null,
                    onClick = {
                        if (currentStartDate != null) {
                            onSave(currentStartDate!!, currentAverageCycleLength)
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.save),

                        )
                }
            }
        }
    }

    if (showDatePicker)
        DatePickerModal(
            startDate = currentStartDate,
            onDateSelected = {
                if (it != null) {
                    currentStartDate = Date(it)
                }
            },
            onDismiss = { showDatePicker = false }
        )
}


@Preview
@Composable
private fun HomeContentPreview() {
    EasyCycleTheme() {
        HomeContent(
            lastCycle = null,
            onOpenSettings = {},
        )
    }
}

@Preview
@Composable
private fun HomeContentPreviewWithList() {
    val lastCycle = Cycle(
        id = 1,
        year = 2023,
        month = 1,
        cycleStart = Date(),
        lengthOfLastCycle = 28
    )
    val cDays = listOf<CDay>(
        CDay(
            id = 1,
            date = Date(),
            cyclesDay = 1,
            phases = ar.subList(0, 2),
            color = "#ff0000"
        ),
        CDay(
            id = 1,
            date = Date(),
            cyclesDay = 2,
            phases = ar.subList(1, 3),
            color = "#00ff00"
        ),
        CDay(
            id = 1,
            date = Date(),
            cyclesDay = 3,
            phases = ar.subList(3, 4),
            color = "#0000ff"
        )
    )
    EasyCycleTheme() {
        HomeContent(
            lastCycle = lastCycle,
            cDays = cDays,
            onOpenSettings = {},
        )
    }
}