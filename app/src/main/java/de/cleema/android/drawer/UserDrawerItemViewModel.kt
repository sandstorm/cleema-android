package de.cleema.android.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.user
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed interface UserDrawerItemUiState {
    object Loading : UserDrawerItemUiState
    data class Content(val user: de.cleema.android.core.models.User) : UserDrawerItemUiState
}

@HiltViewModel
class UserDrawerItemViewModel @Inject constructor(val repository: UserRepository) : ViewModel() {
    var uiState: StateFlow<UserDrawerItemUiState> = repository.getUserStream().map { it?.user }.filterNotNull().map {
        UserDrawerItemUiState.Content(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserDrawerItemUiState.Loading
    )

}
