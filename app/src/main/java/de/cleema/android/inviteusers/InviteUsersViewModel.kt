package de.cleema.android.inviteusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.UserRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface InviteUsersUiState {
    object Loading : InviteUsersUiState
    data class Content(
        val followers: List<de.cleema.android.core.models.SocialUser>,
        val selection: Set<UUID> = setOf()
    ) : InviteUsersUiState

    data class Error(val reason: String) : InviteUsersUiState
}

@HiltViewModel
class InviteUsersViewModel @Inject constructor(repository: UserRepository) : ViewModel() {
    private var selection: MutableStateFlow<Set<UUID>> = MutableStateFlow(setOf())

    val uiState: StateFlow<InviteUsersUiState> =
        selection.combine(repository.getSocialGraphStream()) { selection, result ->
            result.fold(onSuccess = { socialGraph ->
                InviteUsersUiState.Content(socialGraph.followers.filterNot { it.isRequest }.map { it.user }, selection)
            }, onFailure = {
                InviteUsersUiState.Error(it.localizedMessage ?: it.toString())
            })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InviteUsersUiState.Loading
        )

    fun selectUser(userId: UUID) {
        val newSelection = selection.value.toMutableSet()
        if (newSelection.contains(userId)) {
            newSelection.remove(userId)
        } else {
            newSelection.add(userId)
        }
        selection.value = newSelection
    }
}
