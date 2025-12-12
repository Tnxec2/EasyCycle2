package com.kontranik.easycycle.ui.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
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
        }
    )
}

@Composable
fun SettingsContent(
    navigateBack: () -> Unit = {},
    onChangeShowOnStart: (Int) -> Unit = {},
    onChangeDaysOnHome: (Int) -> Unit = {},
    onChangeYearsOnStatistic: (Int) -> Unit = {},
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

            ),
        )
    }
}