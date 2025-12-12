package com.kontranik.easycycle.ui.statistic

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.InsertInvitation
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.model.CDay
import com.kontranik.easycycle.model.LastCycle
import com.kontranik.easycycle.model.StatisticItem
import com.kontranik.easycycle.ui.appbar.AppBar
import com.kontranik.easycycle.ui.appbar.AppBarAction
import com.kontranik.easycycle.ui.shared.CustomDialog
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticScreen(
    statisticViewModel: StatisticViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val statisticList by statisticViewModel.statisticList.collectAsState()
    val averageLength by statisticViewModel.averageLength.collectAsState(DefaultSettings.defaultCycleLength)

    LaunchedEffect(Unit) {
        statisticViewModel.loadStatistic()
    }

    StatisticScreenContent(
        averageLength = averageLength,
        statisticList = statisticList,
        onDelete = { cycle ->
            coroutineScope.launch {
                statisticViewModel.deleteCycleById(cycle.id)
            }
        },
        onImportFromFile = {uri ->
            coroutineScope.launch {
                statisticViewModel.importStatisticFromFile(uri)
            }
        }
    )
}

@Composable
fun StatisticScreenContent(
    averageLength: Int?,
    statisticList: List<StatisticItem>,
    onDelete: (cycle: LastCycle) -> Unit = {_ -> },
    onImportFromFile: (uri: Uri) -> Unit = { _ ->},
) {
    var showHelpDialog by rememberSaveable() {
        mutableStateOf(false)
    }

    var showImportDialog by rememberSaveable() {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.title_statistic,
                appBarActions = listOf{
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = Icons.Default.Download,
                        description = R.string.import_csv_statistic,
                        onClick = { showImportDialog = true }
                    ))
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = Icons.AutoMirrored.Filled.Help,
                        description = R.string.help_statistic,
                        onClick = { showHelpDialog = true }
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
            if (statisticList.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingMedium)
                ) {
                    Text(
                        text = stringResource(R.string.no_statistic_data),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingMedium)
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingMedium)
                ) {
                    averageLength?.let {
                        Text(
                            text = stringResource(R.string.average_cycle_length_statistic, 12,it),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(paddingMedium)
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(horizontal = paddingMedium)
                ) {
                    itemsIndexed(statisticList) { index, item ->
                        StatisticContentItem(
                            item,
                            onDelete = onDelete
                        )
                        if (index != statisticList.size) Box(Modifier.height(paddingSmall))
                    }
                }
            }

            if (showHelpDialog) {
                CustomDialog(
                    title = stringResource(R.string.help),
                    onDismiss = { showHelpDialog = false }
                ) {
                    Text(
                        text = stringResource(R.string.help_statistic),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (showImportDialog) {
                StatisticImportDialog(
                    onImport = {
                        onImportFromFile(it)
                        showImportDialog = false
                    },
                    onDismiss = { showImportDialog = false }
                )
            }
        }
    }
}

@Preview
@Composable
private fun StatisticScreenContentPreviewWithList() {

    val statisticList = listOf<StatisticItem>(
        StatisticItem(
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
                    month = 1,
                    cycleStart = java.util.Date(),
                    lengthOfLastCycle = 28
                )
            ),
            averageCycleLength = 25
            )
    )
    EasyCycleTheme() {
        StatisticScreenContent(
            averageLength = 28,
            statisticList = statisticList
        )
    }
}