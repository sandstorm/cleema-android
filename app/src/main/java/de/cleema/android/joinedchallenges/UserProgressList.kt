package de.cleema.android.joinedchallenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.models.SocialUser
import de.cleema.android.core.models.UserProgress
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun UserProgressList(
    users: List<UserProgress>,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            stringResource(R.string.user_progress_list_title),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier
                .fillMaxWidth()
        ) {
            for (progress in users) {
                UserProgressItem(
                    progress,
                    maxValue,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview("User Progress list", widthDp = 320)
@Composable
fun UserProgressListPreview() {
    CleemaTheme {
        UserProgressList(
            users = listOf(
                UserProgress(
                    0,
                    0,
                    SocialUser(username = "Clara Cleema")
                ),
                UserProgress(
                    25,
                    8,
                    SocialUser(username = "Clara Cleema")
                ),
                UserProgress(
                    25,
                    10,
                    SocialUser(username = "Bernd Brot")
                ),
                UserProgress(
                    25,
                    17,
                    SocialUser(username = "E.T.")
                )
            ),
            maxValue = 52,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
