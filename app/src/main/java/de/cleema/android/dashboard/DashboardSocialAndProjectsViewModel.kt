/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ProjectsRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.models.isJoined
import de.cleema.android.dashboard.DashboardSocialAndProjectsUiState.*
import de.cleema.android.shared.CurrentUserUseCase
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface DashboardSocialAndProjectsUiState {
    data class Success(val projects: List<de.cleema.android.core.models.Project>, val followerCount: Int) :
        DashboardSocialAndProjectsUiState

    data class Error(val reason: String) : DashboardSocialAndProjectsUiState
    object Loading : DashboardSocialAndProjectsUiState
}

@HiltViewModel
class DashboardSocialAndProjectsViewModel @Inject constructor(
    projectsRepository: ProjectsRepository,
    userRepository: UserRepository
) : ViewModel() {
    var uiState: StateFlow<DashboardSocialAndProjectsUiState> =
        projectsRepository.getProjectsStream(regionId = null, joined = null).map { result ->
            result.fold(
                onSuccess = { projects ->
                    val user = CurrentUserUseCase(userRepository).invoke()
                    Success(projects.filter { it.isFaved || it.isJoined }, user.followerCount)
                }, onFailure = {
                    Error(it.localizedMessage ?: it.toString())
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Loading
        )
}
