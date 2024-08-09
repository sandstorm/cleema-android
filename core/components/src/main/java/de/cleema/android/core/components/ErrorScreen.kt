package de.cleema.android.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.Light
import de.cleema.android.core.styling.Tip

@Composable
fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Tip,
            shadowElevation = 4.dp
        ) {
            Column(modifier.padding(16.dp)) {
                Text(message, style = MaterialTheme.typography.bodyMedium, color = Light)
            }
        }
    }
}
