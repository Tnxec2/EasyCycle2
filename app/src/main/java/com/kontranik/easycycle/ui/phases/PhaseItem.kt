package com.kontranik.easycycle.ui.phases

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultPhasesData.Companion.ar
import com.kontranik.easycycle.helper.getTextColorForBackground
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.ui.shared.ConfirmDialog
import com.kontranik.easycycle.ui.shared.ConfirmDialogData
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.theme.paddingMedium
import com.kontranik.easycycle.ui.theme.paddingSmall

@Composable
fun PhaseItem(
    phase: Phase,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showConfirmDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .weight(1f)
                ) {
                    val text = phase.to?.let { to ->
                            stringResource(R.string.phase_from_to, phase.from, to)
                        } ?:
                            stringResource(R.string.phase_from, phase.from)

                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddingSmall)
                    )
                }
                IconButton(
                    onClick = {
                        showConfirmDeleteDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        onEdit()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }
            HorizontalDivider(
                Modifier.padding(bottom = paddingSmall)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall)
            ) {
                Text(
                    text = phase.desc,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = paddingSmall)
                ) {
                    Text(
                        text = stringResource(R.string.color_current),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "",
                        modifier = Modifier
                            .padding(start = paddingMedium)
                            .weight(1f)
                            .background(
                                color = phase.color?.let { Color(it.toColorInt()) } ?: Color.Transparent
                            )
                            .border(1.dp, MaterialTheme.colorScheme.onSurface, RectangleShape)
                    )
                }

                Row(
                    Modifier.fillMaxWidth()
                        .padding(top = paddingSmall)
                ) {
                    Text(
                        text = stringResource(R.string.color_prediction),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "",
                        modifier = Modifier
                            .padding(start = paddingMedium)
                            .weight(1f)
                            .background(
                                color = phase.colorP?.let { Color(it.toColorInt()) } ?: Color.Transparent
                            )
                            .border(1.dp, MaterialTheme.colorScheme.onSurface, RectangleShape)
                    )
                }

                if (phase.color != null || phase.colorP != null) {
                    Text(
                        text = if (phase.markwholephase)  {
                                stringResource(R.string.mark_whole_phase)
                            } else {
                                stringResource(R.string.mark_only_phase_start)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = paddingSmall)
                    )
                }
            }
        }

        if (showConfirmDeleteDialog) {
            ConfirmDialog(
                data = ConfirmDialogData(
                    show = true,
                    title = stringResource(R.string.delete_phase_item),
                    text = stringResource(R.string.are_you_sure_to_delete_phase),
                    onDismiss = { showConfirmDeleteDialog = false },
                    onConfirm = {
                        onDelete()
                        showConfirmDeleteDialog = false
                    }
                )
            )
        }
    }
}

@Preview
@Composable
private fun CDayItemPreview() {
    EasyCycleTheme() {
        Surface() {
            Column(
                Modifier.padding(paddingMedium)
            ) {
                PhaseItem(
                    phase = ar[0]
                )
            }
        }
    }
}