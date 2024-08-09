package de.cleema.android.joinedchallenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ChallengesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface JoinedChallengesUiState {
    data class Content(
        val challenges: List<de.cleema.android.core.models.JoinedChallenge> = listOf(),
        val isLoading: Boolean = false
    ) : JoinedChallengesUiState

    data class Error(val reason: String) : JoinedChallengesUiState
}

@HiltViewModel
class JoinedChallengesViewModel @Inject constructor(repository: ChallengesRepository) :
    ViewModel() {
    val uiState: StateFlow<JoinedChallengesUiState> = repository.getJoinedChallengesStream().map { it ->
        it.fold(onSuccess = { JoinedChallengesUiState.Content(it, isLoading = false) },
            onFailure = { JoinedChallengesUiState.Error(it.localizedMessage ?: "Unknown error") })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JoinedChallengesUiState.Content(listOf(), true)
    )
}
