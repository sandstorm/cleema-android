/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.dashboard.DashboardSocialAndProjectsUiState.*
import java.util.*

@Composable
fun DashboardSocialAndProjectsList(
    modifier: Modifier = Modifier,
    viewModel: DashboardSocialAndProjectsViewModel = hiltViewModel(),
    onProjectClick: (UUID) -> Unit,
    onSocialClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is Success -> DashboardHorizontalList(
            state.projects,
            state.followerCount,
            modifier,
            onProjectClick,
            onSocialClick = onSocialClick
        )
        is Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
        is Loading -> return
    }
}

@Composable
private fun DashboardHorizontalList(
    projects: List<de.cleema.android.core.models.Project>,
    followerCount: Int,
    modifier: Modifier = Modifier,
    onProjectClick: (UUID) -> Unit = {},
    onSocialClick: () -> Unit,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        item {
            SocialItemCard(followerCount = followerCount, modifier.fillParentMaxWidth(0.32f), onClick = onSocialClick)
        }

        itemsIndexed(projects) { index, project ->
            DashboardProjectCard(
                project = project,
                waveMirrored = index % 2 == 0,
                modifier = Modifier.fillParentMaxWidth(0.635f),
                onClick = { onProjectClick(project.id) }
            )
        }
    }
}

