package de.cleema.android.social

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.models.DeepLinking
import de.cleema.android.core.models.SocialGraphItem
import de.cleema.android.social.FollowInvitationUiState.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface FollowInvitationUiState {
    object Loading : FollowInvitationUiState
    data class Following(val item: SocialGraphItem) : FollowInvitationUiState
    data class Error(val reason: String) : FollowInvitationUiState
    object FollowedOnRegistration : FollowInvitationUiState
}

@HiltViewModel
class FollowInvitationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val invitationFlow =
        savedStateHandle.getStateFlow<String?>(DeepLinking.invitationCode, null)
    val uiState: StateFlow<FollowInvitationUiState> = invitationFlow
        .filterNotNull()
        .map { code ->
            userRepository.followInvitation(code)
                .fold(
                    onSuccess = { item ->
                        item?.let { Following(it) } ?: FollowedOnRegistration
                    },
                    onFailure = { Error(it.localizedMessage ?: it.toString()) })
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = Loading
        )
}
