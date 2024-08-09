/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.joinedchallenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.CleemaTheme
import java.util.*


@Composable
fun JoinedChallengeList(
    modifier: Modifier = Modifier,
    viewModel: JoinedChallengesViewModel = hiltViewModel(),
    showsEmptyPlaceholder: Boolean = false,
    onClick: (UUID) -> Unit,
    onCreateClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    JoinedChallengeList(modifier, state, showsEmptyPlaceholder, onClick, onCreateClick)
}

@Composable
fun JoinedChallengeList(
    modifier: Modifier = Modifier,
    state: JoinedChallengesUiState,
    showsEmptyPlaceholder: Boolean = false,
    onClick: (UUID) -> Unit,
    onCreateClick: () -> Unit
) {
    when (state) {
        is JoinedChallengesUiState.Content -> JoinedChallengeList(
            modifier = modifier,
            content = state,
            showsEmptyPlaceholder = showsEmptyPlaceholder,
            onClick = onClick,
            onCreateClick = onCreateClick
        )
        is JoinedChallengesUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
    }
}

@Composable
fun JoinedChallengeList(
    modifier: Modifier = Modifier,
    content: JoinedChallengesUiState.Content,
    showsEmptyPlaceholder: Boolean = false,
    onClick: (UUID) -> Unit,
    onCreateClick: () -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        if (showsEmptyPlaceholder && content.challenges.isEmpty()) {
            item {
                AddJoinedChallengeCard(
                    modifier = modifier
                        .fillParentMaxWidth(0.478f),
                    onTap = onCreateClick
                )
            }
        } else {
            items(content.challenges) { challenge ->
                JoinedChallengeCard(
                    modifier = Modifier.fillParentMaxWidth(0.478f),
                    state = challenge
                ) {
                    onClick(challenge.challenge.id)
                }
            }
        }
    }
}

@Preview("Joined challenge list", widthDp = 340, heightDp = 200)
@Composable
fun JoinedChallengeListPreview() {
    CleemaTheme {
        JoinedChallengeList(
            modifier = Modifier.fillMaxSize(),
            content = JoinedChallengesUiState.Content(
                challenges = listOf(
                    de.cleema.android.core.models.JoinedChallenge(
                        de.cleema.android.core.models.Challenge.of(title = "Ameisen essen"),
                        answers = mapOf(1 to de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED)
                    ),
                    de.cleema.android.core.models.JoinedChallenge(
                        de.cleema.android.core.models.Challenge.of(title = "Müll sammeln"),
                        answers = mapOf(1 to de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED)
                    )
                )
            ),
            onClick = {},
            onCreateClick = {})
    }
}

@Preview("Empty joined challenge list", widthDp = 340, heightDp = 200)
@Composable
fun EmptyJoinedChallengeListPreview() {
    CleemaTheme {
        JoinedChallengeList(
            modifier = Modifier.fillMaxSize(),
            content = JoinedChallengesUiState.Content(
                challenges = listOf()
            ),
            onClick = {},
            onCreateClick = {}
        )
    }
}
