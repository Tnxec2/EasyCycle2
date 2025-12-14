package com.kontranik.easycycle.ui.phases

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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


@Composable
fun PhaseEditDialog(
    phase: Phase,
    onSave: (Phase) -> Unit = {},
    onDismiss: ()-> Unit = {},
) {
    var uiState by remember(phase) { mutableStateOf<PhaseUi>(phase.toUi()) }

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
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    SettingsTextField(
                        value = uiState.from,
                        label = stringResource(R.string.from),
                        maxLines = 1,
                        onChange = {
                            uiState = uiState.copy(from = it)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(
                        modifier = Modifier.width(paddingSmall)
                    )
                    SettingsTextField(
                        value = uiState.to,
                        label = stringResource(R.string.to),
                        maxLines = 1,
                        onChange = {
                            uiState = uiState.copy(to = it)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                SettingsTextField(
                    value = uiState.desc,
                    label = stringResource(R.string.description),
                    maxLines = 3,
                    onChange = {
                        uiState = uiState.copy(desc = it)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                SettingsColor(
                    text = stringResource(R.string.color_current),
                    color = uiState.color?.let { Color(it.toColorInt()) } ?: Color.Transparent,
                    onColorChanged = { color ->
                        uiState = uiState.copy(color = color.toHexCodeWithAlpha())
                    },
                    onSelectDefaultColor = {
                        uiState = uiState.copy(color = phase.color)
                    },
                )
                SettingsColor(
                    text = stringResource(R.string.color_prediction),
                    color = uiState.colorP?.let { Color(it.toColorInt()) } ?: Color.Transparent,
                    onColorChanged = { color ->
                        uiState = uiState.copy(colorP = color.toHexCodeWithAlpha())
                    },
                    onSelectDefaultColor = {
                        uiState = uiState.copy(color = phase.colorP)
                    },
                )
                SettingsCheckbox(
                    value = uiState.markwholephase,
                    label = stringResource(R.string.mark_whole_phase),
                    onChange = {
                        uiState = uiState.copy(markwholephase = it)
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


