/*
 * Created by Kumpels and Friends on 2022-12-07
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.TrophiesRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface TrophiesUiState {
    object Loading : TrophiesUiState
    data class Content(val trophies: List<de.cleema.android.core.models.Trophy>) : TrophiesUiState
    data class Error(val reason: String) : TrophiesUiState
}

fun Result<List<de.cleema.android.core.models.Trophy>>.toUiState(): TrophiesUiState = fold(
    onSuccess = TrophiesUiState::Content,
    onFailure = { TrophiesUiState.Error(reason = it.localizedMessage ?: "Unknown error") }
)

@HiltViewModel
class TrophiesViewModel @Inject constructor(trophiesRepository: TrophiesRepository) :
    ViewModel() {
    val uiState: StateFlow<TrophiesUiState> =
        trophiesRepository.getTrophiesStream().map(Result<List<de.cleema.android.core.models.Trophy>>::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TrophiesUiState.Loading
            )

}
