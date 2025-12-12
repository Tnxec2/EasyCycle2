package com.kontranik.easycycle.ui.calendar

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.database.CycleViewModel
import com.kontranik.easycycle.ui.appbar.AppBar
import com.kontranik.easycycle.ui.settings.SettingsViewModel
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall
import java.util.Calendar
import java.util.Date
import kotlin.collections.emptyList
import kotlin.collections.listOf
import androidx.core.graphics.toColorInt
import com.kontranik.easycycle.helper.getTextColorForBackground
import com.kontranik.easycycle.model.Note
import com.kontranik.easycycle.ui.appbar.AppBarAction
import com.kontranik.easycycle.ui.shared.DatePickerModal


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val matrix: List<List<CalendarDay>> by calendarViewModel.matrix.collectAsState(initial = emptyList())

    val activeCalendarDay by calendarViewModel.activeCalendarDay.collectAsState()



    CalendarContent(
        activeCalendarDay,
        matrix,
        onDay = { date ->
            calendarViewModel.setActiveDate(date)
        },
        onToday = {
            calendarViewModel.activeDateSetToday()
        },
        onPrevMonth = {
            calendarViewModel.activeDateSetToPreviousMonth()
        },
        onNextMonth = {
            calendarViewModel.activeDateSetToNextMonth()
        },
        onSetActiveDate = {
            calendarViewModel.addActiveDateAsCycleStart(Date(it))
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarContent(
    activeDate: CalendarDay,
    matrix: List<List<CalendarDay>>,
    onSetActiveDate: (Long) -> Unit = {},
    onToday: () -> Unit = {},
    onDay: (date: Date) -> Unit = {d -> },
    onPrevMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var currentStartDate by remember { mutableStateOf(Date()) }


    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.title_calendar,
                appBarActions = listOf{
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = Icons.Default.Add,
                        description = R.string.add_cycle,
                        onClick = {
                            currentStartDate = Date()
                            showDatePicker = true
                        }
                    ))
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = true
                )
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingMedium)
                    .padding(bottom = paddingMedium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingMedium)
                ) {
                    IconButton(
                        onClick = {
                            onPrevMonth()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = CalendarViewModel.formatDateToMonth(activeDate.date),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDatePicker = true
                            }
                    )
                    IconButton(
                        onClick = {
                            onToday()
                        }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = Date().date.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)

                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            onNextMonth()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                CalendarComponent(
                    matrix = matrix,
                    onClick = { date -> onDay(date) },
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingMedium)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingMedium)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    color = activeDate.mark?.color?.let {
                                        Color(it.toColorInt())
                                    } ?: Color.Transparent,
                                )
                        ) {
                            Text(
                                text = activeDate.cycleDay?.toString() ?: "",
                                color = activeDate.mark?.color?.let {
                                    getTextColorForBackground(it)
                                } ?: MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(paddingSmall)
                            )
                        }
                        Text(
                            text = CalendarViewModel.formatDateToString(activeDate.date),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (activeDate.canBeAdded) {
                            IconButton(
                                onClick = {
                                    currentStartDate = activeDate.date
                                    showDatePicker = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                            }
                        }
                    }
                    if (activeDate.mark?.note?.notes?.isNotEmpty() == true) {
                        activeDate.mark.note.notes.forEach { note ->
                            Text(
                                text = note,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = paddingSmall)
                            )
                        }
                    }
                }
            }

            if (showDatePicker)
                DatePickerModal(
                    title = stringResource(id = R.string.select_date_for_new_cycle_start),
                    startDate = currentStartDate,
                    onDateSelected = {
                        Log.d("CalendarScreen", "onDateSelected: $it")
                        it?.let { time ->
                            onSetActiveDate(time)
                        }
                    },
                    onDismiss = { showDatePicker = false }
                )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun CalendarContentPreview() {
    val matrix = CalendarViewModel.generateMatrixFromDate(Date())
    val activeDate = CalendarDay(
            date = Date(),
            cycleDay = 5,
            mark = Mark(
                color = "#FF0000",
                note = Note(
                    day = 5,
                    notes = mutableListOf("Note 1", "Note 2")
                )
            )
        )


    EasyCycleTheme() {
        CalendarContent(
            activeDate = activeDate,
            matrix = matrix,
        )
    }
}
