package com.kontranik.easycycle.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.toColorInt
import com.kontranik.easycycle.helper.getTextColorForBackground
import com.kontranik.easycycle.model.Note
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingSmall
import java.util.Date

@Composable
fun CalendarDayBox(
    day: CalendarDay,
    modifier: Modifier = Modifier.Companion,
    onClick: (Date) -> Unit = {}
    ) {

    val bg = if (day.mark?.color != null)
        Color(day.mark.color.toColorInt())
    else
        Color.Transparent
    var color = getTextColorForBackground(
        color = if (day.mark?.color != null)
            day.mark.color
        else
            null,
        defaultColor = if (day.sunday)
            Color.Red
        else
            MaterialTheme.colorScheme.onSurface
    )
    if (!day.currentMonth)
        color = color.copy(alpha = 0.5f)

    val corner = CornerSize(50)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(bg, shape = RoundedCornerShape(
                topStart = if (day.mark?.start == true) corner else CornerSize(0),
                topEnd = if (day.mark?.end == true) corner else CornerSize(0),
                bottomEnd = if (day.mark?.end == true) corner else CornerSize(0),
                bottomStart = if (day.mark?.start == true) corner else CornerSize(0)
            ))
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

@Preview
@Composable
private fun CalendarDayBoxPreview() {
    val note = Note(
        day = 5,
        notes = mutableListOf("Note 1", "Note 2")
    )
    val mark = Mark(
        color = "#FF0000",
        note = Note(
            day = 5,
            notes = mutableListOf("Note 1", "Note 2")
        )
    )
    val dayUnmarked = CalendarDay(
        date = Date(),
        cycleDay = 5
    )
    val dayBetween = CalendarDay(
        date = Date(),
        cycleDay = 5,
        mark = mark
    )
    val dayStart = CalendarDay(
        date = Date(),
        cycleDay = 2,
        mark = mark.copy(start = true)
    )

    val dayEnd = CalendarDay(
        date = Date(),
        cycleDay = 2,
        mark = mark.copy(end = true)
    )

    val dayStartEnd = CalendarDay(
        date = Date(),
        cycleDay = 2,
        mark = mark.copy(end = true, start = true)
    )
    EasyCycleTheme() {
        Surface() {
            Column() {
                CalendarDayBox(
                    day = dayUnmarked
                )
                CalendarDayBox(
                    day = dayStartEnd
                )
                Row() {
                    CalendarDayBox(
                        day = dayStart
                    )
                    CalendarDayBox(
                        day = dayBetween
                    )
                    CalendarDayBox(
                        day = dayEnd
                    )
                }
            }
        }
    }
}