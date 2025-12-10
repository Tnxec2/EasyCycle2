package com.kontranik.easycycle.ui.calendar

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
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
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import kotlin.collections.emptyList
import kotlin.collections.listOf
import androidx.core.graphics.toColorInt
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.helper.TimeHelper
import com.kontranik.easycycle.helper.getTextColorForBackground
import com.kontranik.easycycle.model.Note
import com.kontranik.easycycle.ui.theme.paddingMedium


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    cycleViewModel: CycleViewModel = viewModel(factory = AppViewModelProvider.Factory),
    calendarViewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val matrix: List<List<CalendarDay>> by calendarViewModel.matrix.collectAsState(initial = emptyList())

    val activeCalendarDay by calendarViewModel.activeCalendarDay.collectAsState()

    val context = LocalContext.current

    val cal = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(selectedYear, selectedMonth, selectedDay)
            calendarViewModel.setActiveDate(newCalendar.time)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    )

    CalendarContent(
        activeCalendarDay,
        matrix,
        navigateBack = { coroutineScope.launch { navigateBack() } },
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
        onAdd = {
            cycleViewModel.addCycle(activeCalendarDay)
        },
        openCalendarDialog = {
            datePickerDialog.show()
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarContent(
    activeDate: CalendarDay,
    matrix: List<List<CalendarDay>>,
    navigateBack: () -> Unit = {},
    openCalendarDialog: () -> Unit = {},
    onToday: () -> Unit = {},
    onDay: (date: Date) -> Unit = {d -> },
    onAdd: () -> Unit = {},
    onPrevMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.title_home,
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
                                openCalendarDialog()
                            }
                    )
                    IconButton(
                        onClick = {
                            onToday()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                Calendar(
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
                                .clip(RoundedCornerShape(percent = 50))
                                .background(
                                    color = activeDate.mark?.color?.let {
                                        Color(android.graphics.Color.parseColor(it))
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
                                    onAdd()
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
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(
    matrix: List<List<CalendarDay>>,
    onClick: (Date) -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        Modifier
            .fillMaxWidth()
            .padding(paddingSmall)
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = paddingSmall)
        ) {
            if (matrix.isNotEmpty()) {
                matrix.first().forEach { day ->
                    Text(
                        text = CalendarViewModel.formatDateToDayOfWeek(day.date),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }

        matrix.forEach { row ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { day ->
                    val bg = if (day.mark?.color != null)
                        Color(day.mark.color.toColorInt())
                    else
                        Color.Transparent
                    var color = if (day.sunday)
                        Color.Red
                    else
                        MaterialTheme.colorScheme.onSurface
                    if (!day.currentMonth)
                        color = color.copy(alpha = 0.5f)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .background(bg, shape = CircleShape)
                            .clickable {
                                onClick(day.date)
                            }
                    ) {
                        Text(
                            text = day.date.date.toString(),
                            textAlign = TextAlign.Center,
                            fontWeight = if (day.active) FontWeight.Bold else FontWeight.Normal,
                            style = if (day.active)
                                    MaterialTheme.typography.bodyMedium
                                else
                                    MaterialTheme.typography.bodySmall,
                            color = color,
                            modifier = Modifier
                                .padding(paddingSmall)
                        )
                    }
                }
            }
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
            navigateBack = {},
        )
    }
}
