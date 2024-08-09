package de.cleema.android.joinedchallenges

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.components.AvatarImage
import de.cleema.android.core.models.SocialUser
import de.cleema.android.core.models.UserProgress
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.Dimmed

@Composable
@OptIn(ExperimentalUnitApi::class)
fun UserProgressItem(
    progress: UserProgress,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        AvatarImage(
            image = progress.user.avatar,
            modifier = Modifier.size(48.dp, 48.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                progress.user.username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            LinearProgressIndicator(
                progress = if (maxValue > 0) progress.succeededAnswers.toFloat() / maxValue.toFloat() else 0f,
                color = Dimmed,
                trackColor = Accent.copy(alpha = 0.5f),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
            )
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = TextUnit(value = 16f, TextUnitType.Sp),
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                    )
                ) {
                    append(progress.succeededAnswers.toString())
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = TextUnit(value = 12f, TextUnitType.Sp),
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                    )
                ) {
                    append(" " + stringResource(R.string.answer_challenge_of))
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = TextUnit(value = 16f, TextUnitType.Sp),
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                    )
                ) {
                    append(" $maxValue")
                }
                append(" " + stringResource(R.string.answer_challenge_progress_last))
            }, modifier = Modifier.align(Alignment.End))
        }
    }
}


@Preview("User Progress item", widthDp = 320)
@Composable
fun UserProgressItemPreview() {
    CleemaTheme {
        UserProgressItem(
            UserProgress(
                8,
                25,
                SocialUser(username = "Clara Cleema"),
            ),
            100,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
