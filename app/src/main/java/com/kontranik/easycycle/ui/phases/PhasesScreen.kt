package com.kontranik.easycycle.ui.phases

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultPhasesData.Companion.ar
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.ui.appbar.AppBar
import com.kontranik.easycycle.ui.appbar.AppBarAction
import com.kontranik.easycycle.ui.shared.ConfirmDialog
import com.kontranik.easycycle.ui.shared.ConfirmDialogData
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@Composable
fun PhasesScreen(
    phasesViewModel: PhasesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val state = phasesViewModel.phases.collectAsState()

    PhasesContent(
        phases = state.value,
        onWipePhases = {
            coroutineScope.launch {
                phasesViewModel.wipeCustomPhases()
            }
        },
        onSavePhase = {
            coroutineScope.launch {
                phasesViewModel.savePhase(it)
            }
        },
        onRemovePhase = {
            coroutineScope.launch {
                phasesViewModel.onRemovePhase(it)
            }
        }
    )
}

@Composable
fun PhasesContent(
    phases: List<Phase>,
    onWipePhases: () -> Unit = {},
    onSavePhase: (Phase) -> Unit = {},
    onRemovePhase: (key: Int) -> Unit = {},
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }
    var showWipeDialog by rememberSaveable { mutableStateOf(false) }
    var editPhase by rememberSaveable { mutableStateOf<Phase?>(null) }

    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.title_phases,
                appBarActions = listOf{
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = Icons.Default.Add,
                        description = R.string.add_new_phase,
                        onClick = { editPhase = Phase(
                            key = phases.size,
                            from = 1,
                            desc = "",
                        ) }
                    ))
                    AppBarAction(
                        appBarAction = AppBarAction(
                            vector = Icons.Filled.MoreVert,
                            description = R.string.menu,
                            onClick = { expandedMenu = true }
                        )
                    )
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.load_predefined_phases))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ClearAll,
                                    contentDescription = stringResource(R.string.load_predefined_phases)
                                )
                            },
                            onClick = {
                                onWipePhases();
                                expandedMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

                LazyColumn(
                    modifier = Modifier.padding(horizontal = paddingMedium)
                ) {
                    itemsIndexed(phases) { index, phase ->
                        PhaseItem(
                            phase = phase,
                            onEdit = {
                                editPhase = phase
                            },
                            onDelete = {
                                onRemovePhase(phase.key)
                            }
                        )
                        if (index != phases.size) Box( Modifier.height(paddingSmall))
                    }
                }

            if (showWipeDialog) {
                ConfirmDialog(
                    data = ConfirmDialogData(
                        show = true,
                        title = stringResource(R.string.load_predefined_phases),
                        text = stringResource(R.string.are_you_sure_to_load_predefined_phases),
                        onDismiss = { showWipeDialog = false },
                        onConfirm = {
                            onWipePhases()
                            showWipeDialog = false
                        }
                    )
                )
            }

            editPhase?.let { phase ->
                PhaseEditDialog(
                    phase = phase,
                    onSave = {
                        onSavePhase(it)
                        editPhase = null
                    },
                    onDismiss = {
                        editPhase = null
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun PhasesContentPreviewWithList() {

    val phases = ar.subList(0, 5)
    EasyCycleTheme() {
        PhasesContent(
            phases = phases,
        )
    }
}