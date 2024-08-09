/*
 * Created by Kumpels and Friends on 2022-11-17
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.MagazineRepository
import de.cleema.android.magazine.details.MagazineItemUiState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface MagazineItemUiState {
    data class Success(val magazineItem: de.cleema.android.core.models.MagazineItem) : MagazineItemUiState
    data class Error(val reason: String) : MagazineItemUiState
    object Loading : MagazineItemUiState
}

@HiltViewModel
class MagazineItemViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    var uiState: StateFlow<MagazineItemUiState> =
        savedStateHandle.getStateFlow("newsId", "")
            .map {
                kotlin.runCatching {
                    UUID.fromString(it)
                }.getOrNull()
            }
            .filterNotNull()
            .flatMapLatest { it ->
                magazineRepository.getMagazineItemStream(it).map { result ->
                    result.fold(onSuccess = {
                        Success(it)
                    }, onFailure = {
                        Error(it.localizedMessage ?: it.toString())
                    })
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading
            )

    fun favClicked() {
        when (val state = uiState.value) {
            is Success ->
                viewModelScope.launch {
                    magazineRepository.fav(
                        state.magazineItem.id,
                        state.magazineItem.faved.not()
                    )
                }
            else -> return
        }
    }

    fun markAsRead() {
        when (val state = uiState.value) {
            is Success ->
                viewModelScope.launch {
                    magazineRepository.markAsRead(state.magazineItem.id)
                }
            else -> return
        }
    }
}
