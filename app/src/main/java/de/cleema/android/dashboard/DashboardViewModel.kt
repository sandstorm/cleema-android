/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.SurveysRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import de.cleema.android.dashboard.DashboardItem.*
import de.cleema.android.dashboard.DashboardUiState.Content
import de.cleema.android.survey.SurveyCountUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class DashboardItem {
    SocialAndProjects,
    Quiz,
    Survey,
    Challenges
}

sealed interface DashboardUiState {
    data class Content(val items: List<DashboardItem> = DashboardItem.values().toList()) : DashboardUiState
    data class Error(val reason: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    userRepository: UserRepository,
    surveysRepository: SurveysRepository
) : ViewModel() {
    private val surveyCountUseCase = SurveyCountUseCase(surveysRepository)
    val uiState: StateFlow<DashboardUiState> =
        userRepository.getUserStream().map { it?.user }.filterNotNull()
            .map { user ->
                val count = surveyCountUseCase.invoke()
                Content(
                    listOfNotNull(
                        SocialAndProjects,
                        Quiz,
                        Survey.takeIf { user.acceptsSurveys && count > 0 },
                        Challenges
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Content(listOf(SocialAndProjects, Quiz, Survey, Challenges))
            )
}
