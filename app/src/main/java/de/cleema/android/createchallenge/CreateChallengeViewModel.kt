package de.cleema.android.createchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ChallengesRepository
import de.cleema.android.core.models.EditChallenge
import de.cleema.android.createchallenge.CreateChallengeUiState.ChooseTemplate
import de.cleema.android.shared.CurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface CreateChallengeUiState {
    object ChooseTemplate : CreateChallengeUiState
    data class Error(val reason: String) : CreateChallengeUiState
    data class Edit(val editChallenge: EditChallenge, val canInviteFriends: Boolean = false) : CreateChallengeUiState
    object Done : CreateChallengeUiState
    data class InviteUsers(val editChallenge: EditChallenge, val selectedUserIds: Set<UUID> = setOf()) :
        CreateChallengeUiState
}

val CreateChallengeUiState.canSave: Boolean
    get() = when (this) {
        ChooseTemplate -> false
        is CreateChallengeUiState.Edit -> true
        is CreateChallengeUiState.Error -> false
        is CreateChallengeUiState.Done -> false
        is CreateChallengeUiState.InviteUsers -> true
    }

@HiltViewModel
class CreateChallengeViewModel @Inject constructor(
    private val repository: ChallengesRepository,
    private val currentUserUseCase: CurrentUserUseCase
) : ViewModel() {
    private var _uiState: MutableStateFlow<CreateChallengeUiState> =
        MutableStateFlow(ChooseTemplate)
    val uiState: StateFlow<CreateChallengeUiState> = _uiState

    fun nextClicked() {
        val state = _uiState.value
        when {
            state is CreateChallengeUiState.Edit && state.editChallenge.isPublic
            -> {
                _uiState.value = CreateChallengeUiState.InviteUsers(state.editChallenge)
            }
            state is CreateChallengeUiState.Edit -> save(state.editChallenge)
            state is CreateChallengeUiState.InviteUsers -> save(state.editChallenge, state.selectedUserIds)
            else -> {
                return
            }
        }
    }

    private fun save(challenge: EditChallenge, selection: Set<UUID> = setOf()) {
        viewModelScope.launch {
            repository.createChallenge(challenge.copy(regionId = currentUserUseCase().region.id), selection)
                .onSuccess {
                    _uiState.value = CreateChallengeUiState.Done
                }.onFailure {
                    _uiState.value = CreateChallengeUiState.Error(it.localizedMessage ?: it.toString())
                }
        }
    }

    fun edit(template: EditChallenge) {
        viewModelScope.launch {
            _uiState.value = CreateChallengeUiState.Edit(
                template,
                currentUserUseCase().kind is de.cleema.android.core.models.User.Kind.Remote
            )
        }

    }

    fun inviteUsersWithIds(selection: Set<UUID>) {
        val state = uiState.value
        if (state !is CreateChallengeUiState.InviteUsers) {
            return
        }
        _uiState.value = CreateChallengeUiState.InviteUsers(state.editChallenge, selection)
    }
}
