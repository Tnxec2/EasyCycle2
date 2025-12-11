package com.kontranik.easycycle.ui.calendar

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.easycycle.R
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.database.CycleRepository
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.helper.TimeHelper
import com.kontranik.easycycle.model.Note
import com.kontranik.easycycle.ui.calendar.model.MarkedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Calendar.SUNDAY
import java.util.Date
import java.util.Locale


data class Mark(
    val color: String? = null,
    val start: Boolean = false,
    val end: Boolean = false,
    val note: Note?,
)
data class CalendarDay(
    val date: Date,
    val cycleDay: Int? = null,
    val mark: Mark? = null,
    val sunday: Boolean = false,
    val active: Boolean = false,
    val currentMonth: Boolean = false,
    val canBeAdded: Boolean = false,
)

class CalendarViewModel(
    val app: Application,
    private val cycleRepository: CycleRepository
): AndroidViewModel(app) {

    private val activeDate = MutableStateFlow(Date())
    val activeDateFlow: StateFlow<Date> = activeDate.asStateFlow()

    private val _activeCalendarDay = MutableStateFlow(CalendarDay(date = Date()))
    val activeCalendarDay: StateFlow<CalendarDay> = _activeCalendarDay.asStateFlow()

    val lastCycle = cycleRepository.getLastOneAsFlow()


    val averageLength = cycleRepository.getAverageLength()

    private val _matrix = combineTransform(
        activeDate,
        lastCycle,
        averageLength
    ) {
        activeDate, lastCycle, averageLength ->
        val matrix = generateMatrixFromDate(activeDate)
        if ( lastCycle == null ) {
            emit(matrix)
            return@combineTransform
        }
        if ( averageLength == null ) {
            emit(matrix)
            return@combineTransform
        }
        val result = loadCycleData(activeDate, lastCycle, averageLength, matrix)

        result.flatten().find { calendarDay ->
            calendarDay.date.date == activeDate.date &&
            calendarDay.date.month == activeDate.month &&
            calendarDay.date.year == activeDate.year
        }?. let {
            _activeCalendarDay.value = it
        }

        emit(result)
    }

    val matrix: Flow<List<List<CalendarDay>>> = _matrix

    fun setActiveDate(date: Date) {
        activeDate.value = date
    }

    fun activeDateSetToPreviousMonth() {
        Log.d("CalendarViewModel", "activeDateSetToPreviousMonth")

        activeDate.value = Calendar.getInstance().apply {
                time = activeDate.value
                add(Calendar.MONTH, -1)
            }.time
    }

    fun activeDateSetToNextMonth() {
        Log.d("CalendarViewModel", "activeDateSetToNextMonth")

        activeDate.value = Calendar.getInstance().apply {
            time = activeDate.value
            add(Calendar.MONTH, 1)
        }.time
    }

    fun activeDateSetToday() {
        activeDate.value = Date()
    }

    fun loadCycleData(
        activeDate: Date,
        lastCycle: Cycle?,
        averageLength: Int,
        matrix: List<List<CalendarDay>>): List<List<CalendarDay>> {

        if ( lastCycle == null ) {
            return matrix
        }

        val workCalendar = Calendar.getInstance()
        workCalendar.time = lastCycle.cycleStart

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = activeDate
        tempCalendar.set(Calendar.DAY_OF_MONTH, 0)

        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.time = tempCalendar.time

        maxDateCalendar.add(Calendar.DAY_OF_YEAR, Companion.MAX_DAYS_IN_CALENDAR)

        val tempMarkDate: HashMap<String, MarkedDate> = hashMapOf()
        val tempNotes: HashMap<String, Note> = hashMapOf()
        var lastCycleStart = lastCycle.cycleStart
        var repeated = false
        while (workCalendar.timeInMillis < maxDateCalendar.timeInMillis) {
            var dayCycle: Int = TimeHelper.getDifferenceInDays(workCalendar.time, lastCycleStart) + 1
            val markedData = MarkedDate()
            val key: String = sdfISO.format(workCalendar.time)
            var color: String? = null
            if (dayCycle > 0) {
                if (dayCycle > averageLength) {
                    dayCycle = 1
                    lastCycleStart = workCalendar.time
                    repeated = true
                }
                val dayPhases = PhasesHelper.getPhasesByDay(app, dayCycle)
                tempNotes[key] = Note(
                    day = dayCycle,
                    notes = mutableListOf())

                dayPhases.forEach{ 
                    tempNotes[key]!!.notes.add(it.desc)

                    val tempColor = if (repeated) it.colorP  else it.color
                    if (tempColor != null) {
                        if ( it.markwholephase != null && it.markwholephase == true ) {
                            if ( dayCycle >= it.from && (it.to == null || dayCycle <= it.to!!)) color = tempColor
                        } else if (dayCycle == it.from.toInt()) color = tempColor
                    }
                }
            }
            if ( color != null ) {
                markedData.marked = true
                markedData.color = color
            }
            // if (markedData.marked) tempMarkDate[key] = markedData
            markedData.day = dayCycle
            tempMarkDate[key] = markedData
            workCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return matrix.map { row -> row.map { cell ->
            val f = sdfISO.format(cell.date)
            if (tempMarkDate.containsKey(f)) {
                cell.copy(
                    cycleDay = tempMarkDate[f]?.day,
                    canBeAdded = !( (TimeHelper.isLess(activeDate, lastCycle.cycleStart)
                            || TimeHelper.isEqual(activeDate, lastCycle.cycleStart)
                                )
                            || TimeHelper.isGreat(activeDate, Date())  ),
                    mark = Mark(
                        color = tempMarkDate[f]?.color,
                        start = tempMarkDate[f]?.start ?: false,
                        end = tempMarkDate[f]?.end ?: false,
                        note = tempNotes[f]
                ))
            } else {
                cell
            }
        } }
    }


    fun addActiveDateAsCycleStart(time: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("CalendarViewModel", "addActiveDateAsCycleStart: ${time.toString()}")
            val lastCycle = cycleRepository.getLastOne()

            val lengthOfLastCycle = if ( lastCycle == null ) {
                0
            } else {
                (time.time - lastCycle.cycleStart.time) / (1000 * 60 * 60 * 24)
            }

            Log.d("CalendarViewModel", "addActiveDateAsCycleStart: $lengthOfLastCycle")

            cycleRepository.add(
                Cycle(
                    month = time.month,
                    year = time.year,
                    cycleStart = time,
                    lengthOfLastCycle = lengthOfLastCycle.toInt()
                )
            )
        }
    }

    companion object {
        const val MyCalendarMarkedDateFormat: String = "yyyy-MM-dd"
        const val MyCalendarTitleFormat: String = "MMM yyyy"
        const val MyCalendarTitleDayFormat: String = "dd. MMM yyyy"
        const val MyCalendarWeekdayFormat: String = "EE"

        private const val MAX_DAYS_IN_CALENDAR = 42 // maximal 6 Rows by 7 Days
        private const val ROWS_IN_CALENDAR = 6
        private const val DAYS_IN_ROW = 7

        val sdfISO = SimpleDateFormat(MyCalendarMarkedDateFormat, Locale.US)

        fun formatDateToDayOfWeek(date: Date): String {
            val formatter = SimpleDateFormat(MyCalendarWeekdayFormat, java.util.Locale.getDefault())
            return formatter.format(date)
        }

        fun formatDateToString(date: Date): String {
            val formatter = SimpleDateFormat(MyCalendarTitleDayFormat, java.util.Locale.getDefault())
            return formatter.format(date)
        }

        fun formatDateToMonth(date: Date): String {
            val formatter = SimpleDateFormat(MyCalendarTitleFormat, java.util.Locale.getDefault())
            return formatter.format(date)
        }

        fun isActiveDay(calendar: Calendar, activeDate: Calendar) =
            calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(
                Calendar.MONTH
            ) && calendar.get(Calendar.DAY_OF_MONTH) == activeDate.get(Calendar.DAY_OF_MONTH)

        fun isCurrentMonth(calendar: Calendar, activeDate: Calendar) =
            calendar.get(Calendar.YEAR) == activeDate.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == activeDate.get(
                Calendar.MONTH
            )

        fun generateMatrixFromDate(date: Date): List<List<CalendarDay>> {
            val matrix: MutableList<MutableList<CalendarDay>> = mutableListOf()

            val activeDateCal = Calendar.getInstance()
            activeDateCal.time = date

            val curMonthDate = Calendar.getInstance()
            curMonthDate.time = date
            curMonthDate.set(Calendar.DAY_OF_MONTH, 1)

            val firstDayWeekday = curMonthDate.get(Calendar.DAY_OF_WEEK)

            val prevMonthDate = Calendar.getInstance()
            prevMonthDate.time = date
            prevMonthDate.add(Calendar.MONTH, -1)

            val prevMonthLastDay = prevMonthDate.getActualMaximum(Calendar.DAY_OF_MONTH)

            prevMonthDate.set(Calendar.DAY_OF_MONTH, prevMonthLastDay - firstDayWeekday + 3)

            for (row in 0 until ROWS_IN_CALENDAR) {
                matrix.add(mutableListOf())
                for (col in 0 until DAYS_IN_ROW) {
                    val calDay = CalendarDay(
                        date = prevMonthDate.time,
                        sunday = prevMonthDate.get(Calendar.DAY_OF_WEEK) == SUNDAY,
                        active = isActiveDay(prevMonthDate, activeDateCal),
                        currentMonth = isCurrentMonth(prevMonthDate, activeDateCal),
                    )
                    matrix[row].add(calDay)
                    prevMonthDate.add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            return matrix
        }
    }
}