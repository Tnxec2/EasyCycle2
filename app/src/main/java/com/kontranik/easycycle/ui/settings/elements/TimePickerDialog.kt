package com.kontranik.easycycle.ui.settings.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    content: @Composable () -> Unit
) {

    BasicAlertDialog(
        onDismissRequest = onCancel,
        modifier = modifier
    ) {
        Card(
            Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingMedium)
            ) {
                SettingsTitle(
                    text = title,

                )
                HorizontalDivider(Modifier.padding(vertical = paddingSmall))

                content()

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onCancel
                    ) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                    OutlinedButton(
                        onClick = onConfirm
                    ) {
                        Text(text = stringResource(android.R.string.ok))
                    }
                }
            }
        }
    }
}