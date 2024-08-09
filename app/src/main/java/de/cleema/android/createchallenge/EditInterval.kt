package de.cleema.android.createchallenge

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import de.cleema.android.R
import de.cleema.android.core.models.Challenge

@Composable
fun EditInterval(
    interval: de.cleema.android.core.models.Challenge.Interval,
    onIntervalChanged: (de.cleema.android.core.models.Challenge.Interval) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.edit_change_interval_label),
            style = style,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier.weight(1f))

        IntervalMenu(selectedValue = interval, onValueChanged = onIntervalChanged, style = style)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalMenu(
    selectedValue: de.cleema.android.core.models.Challenge.Interval,
    onValueChanged: (de.cleema.android.core.models.Challenge.Interval) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        modifier = modifier,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                selectedValue.abbreviatedTitle(),
                modifier = Modifier
                    .menuAnchor()
                    .width(IntrinsicSize.Min),
                style = style,
                color = MaterialTheme.colorScheme.onSurface
            )
            ExposedDropdownMenuDefaults.TrailingIcon(
                expanded = expanded
            )
        }
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            de.cleema.android.core.models.Challenge.Interval.values().forEach {
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        Text(
                            it.abbreviatedTitle(),
//                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onValueChanged(it)
                        expanded = false
                    },
                    leadingIcon = {
                        if (it == selectedValue) Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun de.cleema.android.core.models.Challenge.Interval.abbreviatedTitle(): String = when (this) {
    de.cleema.android.core.models.Challenge.Interval.DAILY -> stringResource(R.string.interval_daily)
    de.cleema.android.core.models.Challenge.Interval.WEEKLY -> stringResource(R.string.interval_weekly)
}
