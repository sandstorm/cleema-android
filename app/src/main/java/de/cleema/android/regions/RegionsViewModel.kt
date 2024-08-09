/*
 * Created by Kumpels and Friends on 2022-11-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.regions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.RegionsRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import de.cleema.android.regions.RegionsUiState.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface RegionsUiState {
    data class Success(
        val regions: List<de.cleema.android.core.models.Region>,
        val selection: de.cleema.android.core.models.Region?
    ) : RegionsUiState

    data class Error(val reason: String) : RegionsUiState
    object Loading : RegionsUiState
}

@HiltViewModel
class RegionsViewModel @Inject constructor(
    repository: RegionsRepository,
    userRepository: UserRepository
) : ViewModel() {
    private var selection = MutableStateFlow<de.cleema.android.core.models.Region?>(null)

    val uiState: StateFlow<RegionsUiState> = combine(
        repository.getRegionsStream(), selection, userRepository.getUserStream().map { it?.user }
    ) { result, selection, user ->
        result.fold(onSuccess = {
            Success(it, selection ?: user?.region)
        }, onFailure = {
            Error(it.toString())
        })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Loading
    )

    fun select(regionId: UUID?) {
        when (val state = uiState.value) {
            is Success -> {
                selection.value = state.regions.find { it.id == regionId }
            }
            else -> return
        }
    }
}
