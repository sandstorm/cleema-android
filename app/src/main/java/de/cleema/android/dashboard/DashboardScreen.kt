/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.ContentBackground
import de.cleema.android.dashboard.DashboardItem.*
import de.cleema.android.dashboard.DashboardUiState.Content
import de.cleema.android.dashboard.DashboardUiState.Error
import de.cleema.android.joinedchallenges.JoinedChallengeList
import de.cleema.android.quiz.QuizButton
import de.cleema.android.survey.SurveysList
import java.util.*

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToQuiz: () -> Unit,
    onNavigateToChallenge: (UUID) -> Unit,
    onNavigateToProject: (UUID) -> Unit,
    onNavigateToSocial: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = modifier) {
        ContentBackground(modifier = Modifier.matchParentSize())
        DashboardContent(
            state = uiState,
            onNavigateToQuiz = onNavigateToQuiz,
            onNavigateToChallenge = onNavigateToChallenge,
            onNavigateToProject = onNavigateToProject,
            onNavigateToSocial = onNavigateToSocial
        )
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    state: DashboardUiState,
    onNavigateToQuiz: () -> Unit,
    onNavigateToChallenge: (UUID) -> Unit,
    onNavigateToProject: (UUID) -> Unit,
    onNavigateToSocial: () -> Unit,
) {
    when (state) {
        is Content -> DashboardContent(
            modifier = modifier,
            items = state.items,
            onNavigateToQuiz = onNavigateToQuiz,
            onNavigateToChallenge = onNavigateToChallenge,
            onNavigateToProject = onNavigateToProject,
            onNavigateToSocial = onNavigateToSocial,
        )
        is Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    items: List<DashboardItem>,
    onNavigateToQuiz: () -> Unit,
    onNavigateToChallenge: (UUID) -> Unit,
    onNavigateToProject: (UUID) -> Unit,
    onNavigateToSocial: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(items) { item ->
            when (item) {
                SocialAndProjects -> DashboardSocialAndProjectsList(
                    onProjectClick = onNavigateToProject,
                    onSocialClick = onNavigateToSocial
                )
                Challenges -> JoinedChallengeList(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = onNavigateToChallenge,
                    onCreateClick = {}
                )
                Quiz -> QuizButton(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    onSolveClick = onNavigateToQuiz
                )
                Survey -> SurveysList()
            }
        }
    }
}
