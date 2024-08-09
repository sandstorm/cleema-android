/*
 * Created by Kumpels and Friends on 2022-12-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.TrophiesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import nl.dionsegijn.konfetti.core.Party
import java.util.*
import javax.inject.Inject

sealed interface TrophyUiState {
    object Loading : TrophyUiState
    data class Content(val trophy: de.cleema.android.core.models.Trophy, val party: TrophyViewModel.State) :
        TrophyUiState

    data class Error(val reason: String) : TrophyUiState
}

@HiltViewModel
class TrophyViewModel @Inject constructor(repository: TrophiesRepository, savedState: SavedStateHandle) :
    ViewModel() {
    private val idFlow = savedState.getStateFlow(trophyIdArg, "")
    private val _konfettiState = MutableStateFlow<State>(State.Idle)

    sealed class State {
        class Started(val party: List<Party>) : State()
        object Idle : State()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TrophyUiState> = idFlow
        .map {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getTrophyStream(id).map { it.getOrThrow() }
        }
        .catch {
            TrophyUiState.Error(reason = it.localizedMessage ?: "Unknown error")
        }
        .combine(_konfettiState) { trophy, konfettiState ->
            TrophyUiState.Content(trophy, konfettiState)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrophyUiState.Loading
        )

    fun startKonfetti() {
        _konfettiState.value = State.Started(de.cleema.android.core.components.KonfettiPresets.explode())
    }

    fun endKonfetti() {
        _konfettiState.value = State.Idle
    }
}
