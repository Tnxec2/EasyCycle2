package com.kontranik.easycycle.ui.phases

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.kontranik.easycycle.R
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.model.PhaseUi
import com.kontranik.easycycle.model.toPhase
import com.kontranik.easycycle.model.toUi
import com.kontranik.easycycle.ui.settings.elements.SettingsCard
import com.kontranik.easycycle.ui.settings.elements.SettingsCheckbox
import com.kontranik.easycycle.ui.settings.elements.SettingsColor
import com.kontranik.easycycle.ui.settings.elements.SettingsTextField
import com.kontranik.easycycle.ui.settings.elements.SettingsTitle
import com.kontranik.easycycle.ui.settings.elements.toHexCodeWithAlpha
import com.kontranik.easycycle.ui.theme.paddingBig
import com.kontranik.easycycle.ui.theme.paddingSmall

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhaseEditDialog(
    phase: Phase,
    onSave: (Phase) -> Unit = {},
    onDismiss: ()-> Unit = {},
) {
    val uiState by remember(phase) { mutableStateOf<PhaseUi>(phase.toUi()) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        SettingsCard (
            title = stringResource(R.string.edit_phase),
            modifier = Modifier.padding(paddingSmall)
        )  {
            Column(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                SettingsTextField(
                    value = uiState.from,
                    label = stringResource(R.string.from),
                    maxLines = 1,
                    onChange = {
                        uiState.from = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                SettingsTextField(
                    value = uiState.to,
                    label = stringResource(R.string.to),
                    maxLines = 1,
                    onChange = {
                        uiState.to = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )

                SettingsTextField(
                    value = uiState.desc,
                    label = stringResource(R.string.description),
                    maxLines = 5,
                    onChange = {
                        uiState.desc = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                SettingsColor(
                    text = stringResource(R.string.color_current),
                    color = phase.color?.let { Color(it.toColorInt()) } ?: Color.Transparent,
                    onColorChanged = { color ->
                        uiState.color = color.toHexCodeWithAlpha()
                    },
                    onSelectDefaultColor = {
                        uiState.color = phase.color
                    },
                )
                SettingsColor(
                    text = stringResource(R.string.color_prediction),
                    color = phase.colorP?.let { Color(it.toColorInt()) } ?: Color.Transparent,
                    onColorChanged = { color ->
                        uiState.colorP = color.toHexCodeWithAlpha()
                    },
                    onSelectDefaultColor = {
                        uiState.colorP = phase.colorP
                    },
                )
                SettingsCheckbox(
                    value = uiState.markwholephase,
                    label = stringResource(R.string.mark_whole_phase),
                    onChange = {
                        uiState.markwholephase = it
                    },
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = paddingBig)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onSave(uiState.toPhase())
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.save)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PhaseEditDialogPreview() {
    Surface {
        PhaseEditDialog(
            phase = Phase(
                key = 0,
                from = 1,
                desc = "Test",
                color = "#00ff00",
                colorP = "#00fc00",
                markwholephase = true
            )
        )
    }
}


