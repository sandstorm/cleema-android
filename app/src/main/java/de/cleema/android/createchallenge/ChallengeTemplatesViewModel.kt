package de.cleema.android.createchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ChallengesRepository
import de.cleema.android.core.models.EditChallenge
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface ChallengeTemplatesUiState {
    object Loading : ChallengeTemplatesUiState
    data class Content(val templates: List<EditChallenge>) : ChallengeTemplatesUiState
    data class Error(val reason: String) : ChallengeTemplatesUiState
}

fun Result<List<EditChallenge>>.toUiState(): ChallengeTemplatesUiState = fold(
    onSuccess = ChallengeTemplatesUiState::Content,
    onFailure = {
        ChallengeTemplatesUiState.Error(
            it.localizedMessage ?: it.toString()
        )
    }
)

@HiltViewModel
class ChallengeTemplatesViewModel @Inject constructor(repository: ChallengesRepository) : ViewModel() {
    val uiState: StateFlow<ChallengeTemplatesUiState> =
        repository.getTemplatesStream().map(Result<List<EditChallenge>>::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ChallengeTemplatesUiState.Loading
            )
}
