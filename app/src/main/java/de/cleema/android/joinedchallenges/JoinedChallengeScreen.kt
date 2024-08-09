package de.cleema.android.joinedchallenges

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.cleema.android.core.styling.ContentBackground

@Composable
fun JoinedChallengeScreen(
    modifier: Modifier = Modifier,
    onNotFound: () -> Unit
) {
    Box(modifier = modifier) {
        ContentBackground()
        AnswerChallengeScreen(onNotFound = onNotFound)
    }
}
