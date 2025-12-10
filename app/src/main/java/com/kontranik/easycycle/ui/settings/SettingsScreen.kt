package com.kontranik.easycycle.ui.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.ui.appbar.AppBar
import com.kontranik.easycycle.ui.settings.elements.SettingsCard
import com.kontranik.easycycle.ui.settings.elements.SettingsList
import com.kontranik.easycycle.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val settingsState = settingsViewModel.settingsState.collectAsState()

    SettingsContent(
        navigateBack = { coroutineScope.launch { navigateBack() }},
        settingsState = settingsState.value,
    )
}

@Composable
fun SettingsContent(
    navigateBack: () -> Unit,
    settingsState: Settings,
) {
    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.settings,
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu)
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



        }
    }
}


@Preview
@Composable
private fun SettingsContentPreview() {

        Surface {
            SettingsContent(
                navigateBack = {},
                settingsState = Settings(),
            )
        }


}