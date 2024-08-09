/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.joinedchallenges

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.models.*
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.Light
import de.cleema.android.core.styling.TextInput
import kotlin.random.Random.Default.nextBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinedChallengeCard(
    modifier: Modifier = Modifier,
    state: de.cleema.android.core.models.JoinedChallenge,
    onTap: () -> Unit = {}
) {
    Card(
        modifier = modifier.aspectRatio(1f, false), onClick = onTap,
        colors = CardDefaults.cardColors(
            containerColor = Light
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = state.challenge.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextInput
                )

                Text(
                    text = state.challenge.teaserText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            VerticalProgress(
                modifier = Modifier.weight(0.2f),
                progress = if (state.challenge.kind is Challenge.Kind.Collective) state.collectiveProgress else state.progress
            )
        }
    }
}

@Composable
fun de.cleema.android.core.models.Challenge.Duration.title(): String = when (this) {
    is de.cleema.android.core.models.Challenge.Duration.Days -> stringResource(id = R.string.challenge_days)
    is de.cleema.android.core.models.Challenge.Duration.Weeks -> stringResource(R.string.challenge_weeks)
}

@Preview(name = "Joined challenge", widthDp = 160)
@Composable
fun JoinedChallengeCardPreview() {
    CleemaTheme {
        JoinedChallengeCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            state = de.cleema.android.core.models.JoinedChallenge(
                de.cleema.android.core.models.Challenge.of(
                    title = "Ameisen essen, auch wenns brennt"
                ),
                (1..31).associateWith { if (nextBoolean()) de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED else de.cleema.android.core.models.JoinedChallenge.Answer.FAILED }
            )
        )
    }
}

