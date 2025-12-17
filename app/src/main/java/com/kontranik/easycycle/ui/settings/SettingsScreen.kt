package com.kontranik.easycycle.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.model.toUiState
import com.kontranik.easycycle.ui.appbar.AppBar
import com.kontranik.easycycle.ui.settings.elements.SettingsCard
import com.kontranik.easycycle.ui.settings.elements.SettingsList
import com.kontranik.easycycle.ui.settings.elements.SettingsTextField
import com.kontranik.easycycle.ui.settings.elements.TimePickerDialog
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingSmall
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val settingsState by settingsViewModel.settingsState.collectAsState()

    SettingsContent(
        navigateBack = { coroutineScope.launch { navigateBack() }},
        settings = settingsState,
        onChangeShowOnStart = {
            coroutineScope.launch {
                settingsViewModel.changeShowOnStart(it)
            }
        },
        onChangeDaysOnHome = {
            coroutineScope.launch {
                settingsViewModel.changeDaysOnHome(it)
            }
        },
        onChangeYearsOnStatistic = {
            coroutineScope.launch {
                settingsViewModel.changeYearsOnStatistic(it)
            }
        },
        onChangeNotificationTime = { hour, min ->
            coroutineScope.launch {
                settingsViewModel.changeNotificationTime(hour, min)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    navigateBack: () -> Unit = {},
    onChangeShowOnStart: (Int) -> Unit = {},
    onChangeDaysOnHome: (Int) -> Unit = {},
    onChangeYearsOnStatistic: (Int) -> Unit = {},
    onChangeNotificationTime: (hour: Int, min: Int) -> Unit = {_,_ ->},
    settings: Settings,
) {
    var settingsState by remember { mutableStateOf(settings.toUiState()) }

    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.settings,
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(paddingSmall)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCard(
                title = stringResource(R.string.title_home)
            ) {
                SettingsList<Int>(
                    title = stringResource(R.string.show_on_start),
                    entryTitles = SettingsViewModel.drawerTitles.map { stringResource(it) },
                    entryValues = SettingsViewModel.drawerNavigationIds,
                    defaultValue = settingsState.showOnStart,
                    onChange = { showOnStart ->
                        settingsState = settingsState.copy(
                            showOnStart = showOnStart
                        )
                        onChangeShowOnStart(showOnStart)
                   },
                    showDefaultValue = true,
                    imageVector = SettingsViewModel.drawerIcons[SettingsViewModel.drawerNavigationIds.indexOf(settingsState.showOnStart)]
                )

                SettingsTextField(
                    value = settingsState.daysOnHome,
                    label = stringResource(R.string.days_on_home),
                    onChange = { dayOnHomeString ->
                        settingsState = settingsState.copy(
                            daysOnHome = dayOnHomeString
                        )
                        dayOnHomeString.toIntOrNull()?.let {
                            onChangeDaysOnHome(it)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )
            }
            SettingsCard(
                title = stringResource(R.string.title_statistic),
                modifier = Modifier.padding(top = paddingSmall)
            ) {
                SettingsTextField(
                    value = settingsState.yearsOnStatistic.toString(),
                    label = stringResource(R.string.years_in_statistic),
                    onChange = { yearsOnStatistic ->
                        settingsState = settingsState.copy(
                            yearsOnStatistic = yearsOnStatistic
                        )
                        yearsOnStatistic.toIntOrNull()?.let {
                            onChangeYearsOnStatistic(it)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )
            }

            SettingsCard(
                title = stringResource(R.string.notification),
                modifier = Modifier.padding(top = paddingSmall)
            ) {
                var showTimePicker by rememberSaveable { mutableStateOf(false) }
                val state = rememberTimePickerState(
                    initialHour = settingsState.notificationHour,
                    initialMinute = settingsState.notificationMinute,
                    is24Hour = true
                )
                val formatter = remember { SimpleDateFormat("hh:mm", Locale.getDefault()) }

                val time by rememberSaveable(state.hour, state.minute) {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, state.hour)
                    cal.set(Calendar.MINUTE, state.minute)
                    cal.isLenient = false
                    return@rememberSaveable mutableStateOf(formatter.format(cal.time))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Notification time"
                    )

                    OutlinedButton(
                        modifier = Modifier,
                        onClick = { showTimePicker = true }
                    ) {
                        Text(
                            text = time,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                if (showTimePicker) {
                    TimePickerDialog (
                        title = stringResource(R.string.select_time),
                        onCancel = { showTimePicker = false },
                        onConfirm = {
                            settingsState = settingsState.copy(
                                notificationHour = state.hour,
                                notificationMinute = state.minute
                            )

                            onChangeNotificationTime(state.hour, state.minute)
                            showTimePicker = false
                        },
                    ) {
                        TimePicker(state = state)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun SettingsContentPreview() {
    EasyCycleTheme() {
        SettingsContent(
            settings = Settings(
                showOnStart = 1,
                daysOnHome = 5,
                notificationHour = 9,
                notificationMinute = 15
            ),
        )
    }
}