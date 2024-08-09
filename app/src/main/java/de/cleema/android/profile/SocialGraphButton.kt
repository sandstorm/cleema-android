package de.cleema.android.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.DefaultText

@Composable
fun SocialGraphButton(
    followerCount: Int,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = if (followerCount > 0) modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ) else modifier
        ) {
            Text(
                followerCount.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.alignBy(LastBaseline),
                color = if (isPressed.value) DefaultText.copy(alpha = 0.4f) else DefaultText
            )
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alignBy(LastBaseline),
                color = if (isPressed.value) DefaultText.copy(alpha = 0.4f) else DefaultText
            )
        }
    }
}
