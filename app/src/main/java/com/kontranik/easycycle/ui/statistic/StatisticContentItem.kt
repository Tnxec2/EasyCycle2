package com.kontranik.easycycle.ui.statistic

import android.util.Log
import com.kontranik.easycycle.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.easycycle.database.Cycle
import com.kontranik.easycycle.model.LastCycle
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.ui.calendar.CalendarViewModel
import com.kontranik.easycycle.ui.shared.ConfirmDialog
import com.kontranik.easycycle.ui.shared.ConfirmDialogData
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall
import java.util.Date

@Composable
fun StatisticContentItem(
    statisticItem: StatisticItem,
    onDelete: (cycle: LastCycle) -> Unit = {_ -> },
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
) {
    var expanded by rememberSaveable { mutableStateOf(expanded) }
    var countClicks by rememberSaveable { mutableStateOf(0) }

    var deleteItem  by rememberSaveable() {
        mutableStateOf<LastCycle?>(null)
    }
    var showDeleteDialog  by rememberSaveable() {
        mutableStateOf(false)
    }

    ConfirmDialog(
        data = ConfirmDialogData(
            show = showDeleteDialog,
            title = stringResource(R.string.delete_statistic_item),
            onDismiss = {
                showDeleteDialog = false
                deleteItem = null
            },
            onConfirm = {
                deleteItem?.let { onDelete(it) }
                deleteItem = null
                showDeleteDialog = false
            },
            text = stringResource(R.string.are_you_sure_to_delete_statistic)
        )
    )

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingSmall)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(paddingSmall)
            ) {
                Text(
                    text = statisticItem.year,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = statisticItem.averageCycleLength.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (expanded) {
                HorizontalDivider()
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                ) {

                    
                    statisticItem.items.forEach { cycle ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = paddingSmall)
                                .clickable {
                                    if (deleteItem?.id == cycle.id) {
                                        countClicks += 1
                                        Log.d("StatisticContentItem", "countClicks: $countClicks")
                                        if (countClicks > 5) {
                                            countClicks = 0
                                            showDeleteDialog = true
                                        }
                                    } else {
                                        deleteItem = cycle
                                        countClicks = 1
                                    }
                                }
                        ) {
                            Text(
                                text = CalendarViewModel.formatDateToString(cycle.cycleStart),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = cycle.lengthOfLastCycle.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                }
            }
        }
    }
}

@Preview
@Composable
private fun StatisticContentItemPreview() {
    EasyCycleTheme() {
        Surface() {
            Column() {
                StatisticContentItem(
                    statisticItem = StatisticItem(
                        year = "2023",
                        items = mutableListOf(
                            LastCycle(
                                id = 1,
                                year = 2023,
                                month = 1,
                                cycleStart = java.util.Date(),
                                lengthOfLastCycle = 28
                            ),
                            LastCycle(
                                id = 2,
                                year = 2023,
                                month = 2,
                                cycleStart = Date(),
                                lengthOfLastCycle = 28
                            )
                        ),
                        averageCycleLength = 28
                    ),
                    expanded = true,
                    modifier = Modifier.padding(paddingMedium)
                )
            }
        }
    }
}