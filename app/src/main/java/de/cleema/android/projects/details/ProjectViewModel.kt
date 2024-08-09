/*
 * Created by Kumpels and Friends on 2022-11-14
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ProjectsRepository
import de.cleema.android.di.LocationOpener
import de.cleema.android.di.UrlOpener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface ProjectUiState {
    data class Success(val project: de.cleema.android.core.models.Project) : ProjectUiState
    data class Error(val reason: String) : ProjectUiState
    object Loading : ProjectUiState
}

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    private val opener: UrlOpener,
    savedState: SavedStateHandle,
    private val mapOpener: LocationOpener
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    var uiState =
        savedState.getStateFlow("projectId", "").map {
            kotlin.runCatching {
                UUID.fromString(it)
            }.getOrNull()
        }
            .filterNotNull()
            .flatMapLatest { projectID ->
                projectsRepository.getProjectStream(projectID).map { it ->
                    it.fold(onSuccess = {
                        ProjectUiState.Success(it)
                    }, onFailure = {
                        ProjectUiState.Error(it.toString())
                    })
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = ProjectUiState.Loading
            )

    fun favClicked() {
        when (val state = uiState.value) {
            is ProjectUiState.Success ->
                viewModelScope.launch {
                    projectsRepository.fav(
                        state.project.id,
                        state.project.isFaved.not()
                    )
                }
            else -> return
        }
    }

    fun joinClicked() {
        val state = uiState.value
        if (state is ProjectUiState.Success) {
            viewModelScope.launch {
                if (state.project.goal.isJoined) {
                    projectsRepository.leave(state.project.id)
                } else {
                    projectsRepository.join(state.project.id)
                }
            }
        }
    }

    fun onPartnerClicked() {
        val state = uiState.value
        if (state is ProjectUiState.Success) {
            viewModelScope.launch {
                opener.openUrl(state.project.partner.url)
            }
        }
    }

    fun onLocationClicked() {
        val state = uiState.value
        if (state is ProjectUiState.Success) {
            state.project.location?.let { location ->
                viewModelScope.launch {
                    mapOpener.openLocation(location)
                }
            }

        }
    }
}
