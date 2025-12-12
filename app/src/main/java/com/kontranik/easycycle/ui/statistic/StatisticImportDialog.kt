package com.kontranik.easycycle.ui.statistic

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingBig
import com.kontranik.easycycle.ui.theme.paddingSmall

@Composable
fun StatisticImportDialog(
    onImport: (uri: Uri) -> Unit = {},
    onDismiss: ()-> Unit = {},
) {

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            onImport(it)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        SettingsCard (
            title = stringResource(R.string.import_statistic),
            modifier = Modifier.padding(paddingSmall)
        )  {
            Column(
                modifier = Modifier
                    .padding(vertical = paddingSmall)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Text(
                    text = stringResource(R.string.import_description),
                    style = MaterialTheme.typography.bodyMedium,
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
                            text = stringResource(android.R.string.cancel)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            openDocumentLauncher.launch(arrayOf("text/*"))
                        }
                    ) {
                        Text(
                            text = stringResource(android.R.string.ok)
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
    EasyCycleTheme() {
        StatisticImportDialog()
    }
}


