package de.cleema.android.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.models.User
import de.cleema.android.shared.CurrentUserUseCase
import de.cleema.android.social.InviteLinkUiState.Invite
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

sealed interface InviteLinkUiState {
    object Loading : InviteLinkUiState
    data class Invite(val referralCode: String) : InviteLinkUiState
    object InvalidUserKind : InviteLinkUiState
}

@HiltViewModel
class InviteLinkViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
    private val client: SocialClient
) : ViewModel() {
    val uiState: StateFlow<InviteLinkUiState> = flow {
        val user = currentUserUseCase()
        emit(
            when (user.kind) {
                de.cleema.android.core.models.User.Kind.Local -> Invite("")
                is de.cleema.android.core.models.User.Kind.Remote -> Invite(user.referralCode)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InviteLinkUiState.Loading
    )

    fun getUserKind (): User.Kind {
        val user = runBlocking { currentUserUseCase() }
        return user.kind
    }

    fun inviteUsersClicked() {
        val state = uiState.value
        if (state is Invite) viewModelScope.launch {
            client.invite(state.referralCode)
        }
    }
}
