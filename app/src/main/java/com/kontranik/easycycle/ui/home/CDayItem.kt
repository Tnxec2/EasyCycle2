package com.kontranik.easycycle.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.easycycle.constants.DefaultPhasesData.Companion.ar
import com.kontranik.easycycle.helper.getTextColorForBackground
import com.kontranik.easycycle.model.CDay
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.ui.calendar.CalendarViewModel
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall

@Composable
fun CDayItem(
    cday: CDay,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingSmall)
            ) {
                Box(
                    modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        color = cday.color?.let {
                            Color(android.graphics.Color.parseColor(it))
                        } ?: Color.Transparent,
                    )
                ) {
                    Text(
                        text = cday.cyclesDay.toString(),
                        color = cday.color?.let {
                            getTextColorForBackground(it)
                        } ?: MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(paddingSmall)
                    )
                }
                Text(
                    text = CalendarViewModel.formatDateToString(cday.date),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall)
            ) {
                cday.phases.forEach { phase ->
                    Text(
                        text = phase.desc,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = paddingSmall)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CDayItemPreview() {
    EasyCycleTheme() {
        Surface() {
            Column() {
                CDayItem(
                    cday = CDay(
                        id = 1,
                        date = java.util.Date(),
                        cyclesDay = 1,
                        phases = ar.subList(0, 2),
                        color = "#ffdec2"
                    ),
                    modifier = Modifier.padding(paddingMedium)
                )

                CDayItem(
                    cday = CDay(
                        id = 1,
                        date = java.util.Date(),
                        cyclesDay = 5,
                        phases = ar.subList(5, 7),
                        color = "#ff0000"
                    ),
                    modifier = Modifier.padding(paddingMedium)
                )
            }
        }
    }
}