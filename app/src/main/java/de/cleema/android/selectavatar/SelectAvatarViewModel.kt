/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.selectavatar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.AvatarRepository
import de.cleema.android.core.models.IdentifiedImage
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface SelectAvatarUiState {
    object Loading : SelectAvatarUiState
    data class Content(val avatars: List<IdentifiedImage>, val selection: IdentifiedImage? = null) : SelectAvatarUiState
    data class Error(val reason: String) : SelectAvatarUiState
}

@HiltViewModel
class SelectAvatarViewModel @Inject constructor(repository: AvatarRepository) : ViewModel() {
    private var selection: MutableStateFlow<UUID?> = MutableStateFlow(null)
    fun select(avatarId: UUID?) {
        selection.value = avatarId
    }

    val uiState: StateFlow<SelectAvatarUiState> = repository.getAvatarsStream()
        .combine(selection) { avatars, selectedId ->
            avatars.fold(onSuccess = { result ->
                SelectAvatarUiState.Content(result, selection = result.find { it.id == selectedId })
            }, onFailure = {
                SelectAvatarUiState.Error(reason = it.localizedMessage ?: it.toString())
            })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SelectAvatarUiState.Loading
        )
}
