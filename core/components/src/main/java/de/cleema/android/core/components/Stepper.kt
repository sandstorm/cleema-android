package de.cleema.android.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    decreaseIcon: @Composable () -> Unit,
    increaseIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedRange<Int> = 0..Int.MAX_VALUE,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()

        Spacer(modifier.weight(1f))

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onValueChange(value.dec().coerceIn(valueRange)) },
                    enabled = value > valueRange.start,
                    content = decreaseIcon
                )

                Divider(
                    modifier = Modifier
                        .fillMaxHeight(0.75f)
                        .width(1.dp)
                )

                IconButton(
                    onClick = { onValueChange(value.inc().coerceIn(valueRange)) },
                    enabled = value < valueRange.endInclusive,
                    content = increaseIcon
                )
            }
        }
    }
}
