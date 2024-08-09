/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ProjectsRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.projects.list.ProjectsUiState.*
import de.cleema.android.shared.RegionForCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface ProjectsUiState {
    data class Content(val projects: List<de.cleema.android.core.models.Project>) : ProjectsUiState
    data class Error(val reason: String) : ProjectsUiState
    object Loading : ProjectsUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectsRepository: ProjectsRepository,
    userRepository: UserRepository
) : ViewModel() {
    private val regionForCurrentUserUseCase = RegionForCurrentUserUseCase(userRepository)
    private var selectedRegion = MutableStateFlow<UUID?>(null)

    val uiState: StateFlow<ProjectsUiState> =
        selectedRegion
            .onStart { emit(regionForCurrentUserUseCase().id) }
            .filterNotNull()
            .flatMapLatest { regionId ->
                projectsRepository.getProjectsStream(regionId = regionId).map { result ->
                    result.fold(onSuccess = ::Content, onFailure = {
                        Error(it.localizedMessage ?: it.toString())
                    })
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading
            )

    fun favClicked(uuid: UUID) {
        when (val state = uiState.value) {
            is Content ->
                state.projects.find { it.id == uuid }?.let { project ->
                    viewModelScope.launch {
                        projectsRepository.fav(
                            project.id,
                            project.isFaved.not()
                        )
                    }
                }
            else -> return
        }
    }

    fun setRegion(regionId: UUID) {
        selectedRegion.value = regionId
    }
}
