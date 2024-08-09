package de.cleema.android.createchallenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.TextInput

@Composable
fun EditStep(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        de.cleema.android.core.components.Stepper(
            value = value,
            onValueChange = onValueChange,
            decreaseIcon = { Icon(painter = painterResource(R.drawable.remove_icon), contentDescription = null) },
            increaseIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            valueRange = 1..100,
            backgroundColor = TextInput.copy(alpha = 0.25f)
        ) {
            Text(
                stringResource(id = R.string.steps_label_format, value),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
