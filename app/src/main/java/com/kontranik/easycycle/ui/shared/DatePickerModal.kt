package com.kontranik.easycycle.ui.shared

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    title: String = "Select date",
    startDate: Date? = Date(),
    minDate: Date? = null,
    maxDate: Date? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object: SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                minDate?.let { utcTimeMillis >= it.time } ?: true &&
                        maxDate?.let { utcTimeMillis <= it.time } ?: true
        }
    )

    LaunchedEffect(startDate) {
        startDate?.time?.let { time ->
            datePickerState.selectedDateMillis = time
        }
    }


    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(paddingMedium)
    ) {

        DatePicker(
            state = datePickerState,
            title = { Text(title) },
            modifier = Modifier.padding(paddingMedium)
        )
    }
}

@Preview
@Composable
private fun DatePickerModalPreview() {
    EasyCycleTheme() {
        Surface() {
            DatePickerModal(
                title = "Test picker",
                onDateSelected = {},
                onDismiss = {}
            )
        }
    }

}