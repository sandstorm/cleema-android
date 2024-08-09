package de.cleema.android.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface SocialGraphUiState {
    object Loading : SocialGraphUiState
    data class Content(
        val followers: List<de.cleema.android.core.models.SocialGraphItem> = listOf(),
        val following: List<de.cleema.android.core.models.SocialGraphItem> = listOf(),
        val alertItem: de.cleema.android.core.models.SocialGraphItem? = null
    ) :
        SocialGraphUiState

    data class Error(val reason: String) : SocialGraphUiState
}

@HiltViewModel
class SocialGraphViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private var alertUserId = MutableStateFlow<UUID?>(null)

    fun onRemoveUserClick(userId: UUID) {
        alertUserId.value = userId
    }

    fun dismissAlert() {
        alertUserId.value = null
    }

    fun confirmUnfollowing() {
        val state = uiState.value
        if (state is SocialGraphUiState.Content && state.alertItem != null) {
            viewModelScope.launch {
                repository.unfollow(state.alertItem.id)
            }
            alertUserId.value = null
        }
    }

    val uiState: StateFlow<SocialGraphUiState> =
        repository.getSocialGraphStream().combine(alertUserId) { graphResult, alertId ->
            graphResult.toUiState(alertId)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SocialGraphUiState.Loading
            )

}

fun Result<de.cleema.android.core.models.SocialGraph>.toUiState(alertId: UUID?): SocialGraphUiState =
    fold(
        onSuccess = { socialGraph ->
            val followers = socialGraph.followers.filterNot { it.isRequest }
            val following = socialGraph.following.filterNot { it.isRequest }
            SocialGraphUiState.Content(
                followers,
                following,
                alertItem = (followers + following).find { it.id == alertId }
            )
        },
        onFailure = { SocialGraphUiState.Error(reason = it.localizedMessage ?: it.toString()) }
    )
