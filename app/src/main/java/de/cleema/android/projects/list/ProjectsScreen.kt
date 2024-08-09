/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.ContentBackground
import de.cleema.android.regions.RegionsList
import java.util.*


@Composable
fun ProjectsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = hiltViewModel(),
    onProjectClick: (UUID) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier) {
        ContentBackground(modifier = Modifier.fillMaxSize())
        ProjectsContent(
            uiState,
            Modifier.fillMaxSize(),
            viewModel::setRegion,
            onProjectClick,
            viewModel::favClicked
        )
    }
}

@Composable
fun ProjectsContent(
    state: ProjectsUiState,
    modifier: Modifier = Modifier,
    onSelectRegion: (UUID) -> Unit = {},
    onProjectClick: (UUID) -> Unit = {},
    onFavClick: (UUID) -> Unit
) {
    when (state) {
        is ProjectsUiState.Content -> Column(modifier = modifier.fillMaxWidth()) {
            RegionsList(
                modifier = Modifier.padding(horizontal = 20.dp),
                onSelectRegion = { onSelectRegion(it.id) }
            )
            ProjectsList(
                state.projects,
                Modifier,
                onProjectClick,
                onFavClick
            )
        }
        is ProjectsUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
        is ProjectsUiState.Loading -> de.cleema.android.core.components.LoadingScreen()
    }
}

@Composable
fun ProjectsList(
    projects: List<de.cleema.android.core.models.Project>,
    modifier: Modifier = Modifier,
    onProjectClick: (UUID) -> Unit = {},
    onFavClick: (UUID) -> Unit = {},
) {
    LazyColumn(
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        items(projects) { project ->
            ProjectCard(
                project = project,
                onClick = { onProjectClick(project.id) },
                onFavClick = { onFavClick(project.id) }
            )
        }
    }
}

