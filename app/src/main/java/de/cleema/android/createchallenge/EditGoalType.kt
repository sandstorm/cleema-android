package de.cleema.android.createchallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.cleema.android.R
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.createchallenge.Rows.MEASUREMENT
import de.cleema.android.createchallenge.Rows.STEPS

enum class Rows { STEPS, MEASUREMENT }

@Composable
fun EditGoalType(
    modifier: Modifier = Modifier,
    goalType: de.cleema.android.core.models.Challenge.GoalType,
    onGoalTypeChanged: (de.cleema.android.core.models.Challenge.GoalType) -> Unit,
) {
    var selectedRow by remember { mutableStateOf(STEPS) }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding()) {
            TabRow(
                selectedTabIndex = Rows.values().indexOf(selectedRow),
                contentColor = DefaultText
            ) {
                Rows.values().forEach { row ->
                    Tab(
                        text = {
                            Text(
                                text = row.title(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            if (row != selectedRow) {
                                when (row) {
                                    STEPS -> {
                                        onGoalTypeChanged(
                                            de.cleema.android.core.models.Challenge.GoalType.Steps(
                                                goalType.valueCount
                                            )
                                        )
                                    }
                                    MEASUREMENT -> {
                                        onGoalTypeChanged(
                                            de.cleema.android.core.models.Challenge.GoalType.Measurement(
                                                goalType.valueCount
                                            )
                                        )
                                    }
                                }
                                selectedRow = row
                            }
                        },
                        selected = (row == selectedRow),
                        selectedContentColor = DefaultText,
                    )
                }
            }
            when (goalType) {
                is de.cleema.android.core.models.Challenge.GoalType.Steps -> EditStep(
                    value = goalType.count,
                    onValueChange = { onGoalTypeChanged(de.cleema.android.core.models.Challenge.GoalType.Steps(it)) },
                    modifier = Modifier.fillMaxWidth()
                )
                is de.cleema.android.core.models.Challenge.GoalType.Measurement -> EditMeasurement(
                    value = goalType.count,
                    unit = goalType.unit,
                    onValueChanged = {
                        onGoalTypeChanged(
                            de.cleema.android.core.models.Challenge.GoalType.Measurement(
                                it,
                                goalType.unit
                            )
                        )
                    },
                    onUnitChanged = {
                        onGoalTypeChanged(
                            de.cleema.android.core.models.Challenge.GoalType.Measurement(
                                goalType.count,
                                it
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun Rows.title(): String = when (this) {
    STEPS -> stringResource(R.string.goaltype_steps)
    MEASUREMENT -> stringResource(R.string.goaltype_measurement)
}

@Preview("Goal type", widthDp = 320, heightDp = 400)
@Composable
fun EditGoalTypePreview() {
    EditGoalType(
        modifier = Modifier.fillMaxSize(),
        goalType = de.cleema.android.core.models.Challenge.GoalType.Steps(42),
        onGoalTypeChanged = {})
}
