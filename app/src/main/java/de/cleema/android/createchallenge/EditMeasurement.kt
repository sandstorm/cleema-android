package de.cleema.android.createchallenge

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.models.Challenge.Unit.KILOGRAMS
import de.cleema.android.core.models.Challenge.Unit.KILOMETERS
import de.cleema.android.core.styling.TextInput

@Composable
private fun de.cleema.android.core.models.Challenge.Unit.abbreviatedTitle(): String = when (this) {
    KILOMETERS -> stringResource(R.string.kilometer_abbreviated)
    KILOGRAMS -> stringResource(R.string.kilogram_abbreviated)
}

@Composable
fun EditMeasurement(
    modifier: Modifier = Modifier,
    value: Int,
    unit: de.cleema.android.core.models.Challenge.Unit,
    onValueChanged: (Int) -> Unit,
    onUnitChanged: (de.cleema.android.core.models.Challenge.Unit) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.edit_measurement_unit_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier.weight(1f))

            UnitMenu(selectedUnit = unit, onUnitChanged = onUnitChanged)
        }

        de.cleema.android.core.components.Stepper(
            value = value,
            onValueChange = onValueChanged,
            decreaseIcon = { Icon(painter = painterResource(R.drawable.remove_icon), contentDescription = null) },
            increaseIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            valueRange = 1..100,
            backgroundColor = TextInput.copy(alpha = 0.25f)
        ) {
            Text(
                "$value ${unit.abbreviatedTitle()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitMenu(
    selectedUnit: de.cleema.android.core.models.Challenge.Unit,
    onUnitChanged: (de.cleema.android.core.models.Challenge.Unit) -> Unit,
    modifier: Modifier = Modifier
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
                selectedUnit.abbreviatedTitle(),
                modifier = Modifier
                    .menuAnchor()
                    .width(IntrinsicSize.Min),
                style = MaterialTheme.typography.bodySmall,
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
            de.cleema.android.core.models.Challenge.Unit.values().forEach {
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
                        onUnitChanged(it)
                        expanded = false
                    },
                    leadingIcon = {
                        if (it == selectedUnit) Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
