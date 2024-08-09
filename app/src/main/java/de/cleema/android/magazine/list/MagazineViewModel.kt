/*
 * Created by Kumpels and Friends on 2022-11-17
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.MagazineRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import de.cleema.android.magazine.list.MagazineUiState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface MagazineUiState {
    data class Success(val magazineItems: List<de.cleema.android.core.models.MagazineItem>) : MagazineUiState
    data class Error(val reason: String) : MagazineUiState
    object Loading : MagazineUiState
}

@HiltViewModel
class MagazineViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
    userRepository: UserRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    var uiState: StateFlow<MagazineUiState> = userRepository.getUserStream().map { it?.user }.filterNotNull()
        .flatMapLatest {
            magazineRepository.getMagazineItemsStream(it.region.id)
        }
        .map { it ->
            it.fold(onSuccess = { items ->
                Success(items.sortedByDescending { it.date })
            }, onFailure = {
                Error(it.localizedMessage ?: it.toString())
            })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Loading
        )

    fun favClicked(uuid: UUID) {
        when (val state = uiState.value) {
            is Success ->
                state.magazineItems.find { it.id == uuid }?.let { item ->
                    viewModelScope.launch {
                        magazineRepository.fav(
                            item.id,
                            item.faved.not()
                        )
                    }
                }
            else -> return
        }
    }
}
