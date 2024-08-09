/*
 * Created by Kumpels and Friends on 2022-12-08
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.AuthRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.data.user
import de.cleema.android.login.UserAuthenticationUiState.Credentials
import de.cleema.android.login.UserAuthenticationUiState.Success
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserAuthenticationUiState {
    data class Credentials(
        val name: String = "",
        val password: String = "",
        val isLoading: Boolean,
        val errorMessage: String? = null
    ) : UserAuthenticationUiState

    object Success : UserAuthenticationUiState
}

val Credentials.canLogin: Boolean
    get() = (name.isNotBlank() && password.isNotBlank()) && !isLoading

@HiltViewModel
class UserAuthenticationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) :
    ViewModel() {
    private var _uiState: MutableStateFlow<UserAuthenticationUiState> =
        MutableStateFlow(Credentials(isLoading = false))

    val uiState: StateFlow<UserAuthenticationUiState> = userRepository
        .getUserStream().map { it?.user }
        .distinctUntilChangedBy { it?.id }
        .combine(_uiState) { user: de.cleema.android.core.models.User?, uiState ->
            when (uiState) {
                Success -> {
                    if (user == null) {
                        Credentials(isLoading = false)
                    } else {
                        uiState
                    }
                }
                else -> {
                    uiState
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Credentials(isLoading = false)
        )

    fun enterName(name: String) = when (val state = uiState.value) {
        is Credentials -> {
            _uiState.value = state.copy(name = name)
        }
        else -> {}
    }

    fun enterPassword(password: String) = when (val state = uiState.value) {
        is Credentials -> {
            _uiState.value = state.copy(password = password)
        }
        else -> {}
    }

    fun login() {
        val state = uiState.value as? Credentials ?: return
        if (!state.canLogin) {
            return
        }
        _uiState.value = state.copy(isLoading = true)
        viewModelScope.launch {
            authRepository.login(username = state.name, password = state.password).fold(onSuccess = {
                userRepository.save(Valid(it))
                _uiState.value = Success
            }, onFailure = { error ->
                _uiState.update {
                    state.copy(isLoading = false, errorMessage = error.message)
                }
            })
        }
    }

    fun onClearClick() {
        val state = uiState.value as? Credentials ?: return
        _uiState.value = state.copy(errorMessage = null)
    }
}
