package com.kontranik.easycycle.ui.calendar

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingSmall
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarComponent(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingSmall)
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

        matrix.forEachIndexed { rowIndex, row ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp)
            ) {
                row.forEachIndexed { cell, day ->
                    CalendarDayBox(
                        day,
                        modifier = Modifier.weight(1f),
                        onClick = onClick
                    )
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun CalendarComponentPreview() {
    val matrix = CalendarViewModel.generateMatrixFromDate(Date())

    EasyCycleTheme() {
        Surface() {
            CalendarComponent(
                matrix = matrix,
            )
        }
    }
}


