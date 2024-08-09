/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.SurveysRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import de.cleema.android.di.UrlOpener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface SurveyUiState {
    object Loading : SurveyUiState
    object Denied : SurveyUiState
    data class Content(val surveys: List<de.cleema.android.core.models.Survey>) : SurveyUiState
    data class Error(val reason: String) : SurveyUiState
}

@HiltViewModel
class SurveysViewModel @Inject constructor(
    private val surveysRepository: SurveysRepository,
    private val urlOpener: UrlOpener,
    userRepository: UserRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SurveyUiState> =
        userRepository.getUserStream().map { it?.user }.filterNotNull()
            .flatMapLatest { user ->
                if (user.acceptsSurveys) {
                    surveysRepository.getSurveysStream().map {
                        it.fold(
                            onSuccess = { surveys ->
                                SurveyUiState.Content(surveys)
                            },
                            onFailure = { error -> SurveyUiState.Error(error.localizedMessage ?: "Unknown error") }
                        )
                    }
                } else {
                    flowOf(SurveyUiState.Denied)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SurveyUiState.Loading
            )

    fun onSurveyClicked(id: UUID) {
        when (val state = uiState.value) {
            is SurveyUiState.Content -> {
                val survey = state.surveys.find { it.id == id }
                survey?.let {
                    viewModelScope.launch {
                        when (it.state) {
                            is de.cleema.android.core.models.SurveyState.Evaluation -> surveysRepository.evaluate(id)
                            is de.cleema.android.core.models.SurveyState.Participation -> surveysRepository.participate(
                                id
                            )
                        }
                        urlOpener.openUrl(survey.state.uri)
                    }
                }
            }
            else -> return
        }
    }
}
