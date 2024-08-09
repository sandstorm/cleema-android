package de.cleema.android.partnerchallenges

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ChallengesRepository
import de.cleema.android.core.models.Challenge
import de.cleema.android.di.UrlOpener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface PartnerChallengeUiState {
    object Loading : PartnerChallengeUiState
    data class Success(val challenge: de.cleema.android.core.models.Challenge, val showsAlert: Boolean = false) :
        PartnerChallengeUiState

    data class NotFound(val message: String) : PartnerChallengeUiState
}

val PartnerChallengeUiState.currentChallenge: de.cleema.android.core.models.Challenge?
    get() = when (this) {
        is PartnerChallengeUiState.Success -> challenge
        else -> null
    }

@HiltViewModel
class PartnerChallengeViewModel @Inject constructor(
    private val repository: ChallengesRepository,
    savedStateHandle: SavedStateHandle,
    private val opener: UrlOpener
) :
    ViewModel() {
    private var showsAlertState = MutableStateFlow(false)
    private val idFLow = savedStateHandle.getStateFlow("challengeId", "")

    fun socialButtonTapped() {
        uiState.value.currentChallenge?.let { challenge ->
            if (!challenge.joined) {
                viewModelScope.launch {
                    repository.joinChallenge(challenge.id)
                }
            } else {
                showsAlertState.value = true
            }
        }

    }

    fun confirmLeave() {
        uiState.value.currentChallenge?.let {
            viewModelScope.launch {
                repository.leaveChallenge(it.id)
            }
        }
        dismissAlert()
    }

    fun dismissAlert() {
        showsAlertState.value = false
    }

    fun partnerTapped() {
        uiState.value.currentChallenge?.let {
            when (val kind = it.kind) {
                is Challenge.Kind.Partner -> {
                    viewModelScope.launch {
                        opener.openUrl(kind.partner.url)
                    }
                }

                is Challenge.Kind.Collective -> {
                    viewModelScope.launch {
                        opener.openUrl(kind.partner.url)
                    }
                }

                else -> {
                    // We need the empty else bnach for some reason
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PartnerChallengeUiState> =
        idFLow
            .map {
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            .filterNotNull()
            .flatMapLatest { id ->
                repository.getChallengeStream(id)
                    .combine(showsAlertState) { challenge, showsAlert ->
                        challenge.fold(
                            {
                                PartnerChallengeUiState.Success(
                                    it, showsAlert
                                )
                            }, {
                                PartnerChallengeUiState.NotFound(message = it.message ?: "Unknown error")
                            }
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PartnerChallengeUiState.Loading
            )
}
