/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.AuthRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.Credentials
import de.cleema.android.core.models.DeepLinking
import de.cleema.android.core.models.User
import de.cleema.android.login.LoginUiState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    object Loading : LoginUiState
    object AuthenticateUser : LoginUiState
    data class CreateNewUser(val invitationCode: String? = null) : LoginUiState
    data class LoggedIn(val user: User) : LoginUiState
    data class PendingConfirmation(val credentials: Credentials) : LoginUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val savedState: SavedStateHandle
) : ViewModel() {
    private var showsLogin = MutableSharedFlow<Boolean?>()

    //    private val codeFlow = MutableStateFlow<String?>(null)
    private val codeFlow =
        savedState.getStateFlow<String?>(DeepLinking.confirmationCode, null)
    private val invitationCodeFlow =
        savedState.getStateFlow<String?>(DeepLinking.invitationCode, null)

    fun switchToLogin() {
        viewModelScope.launch {
            showsLogin.emit(true)
        }
    }

    fun dismissLogin() {
        viewModelScope.launch {
            showsLogin.emit(false)
        }
    }

    fun reset() {
        viewModelScope.launch {
            userRepository.deleteUser()
        }
    }

    //    data class Value(val user: User?, val showsLogin: Boolean)
    val uiState: StateFlow<LoginUiState> =
        merge(
            userRepository
                .getUserStream()
                .filterIsInstance<UserValue.Pending>()
                .flatMapLatest { pending ->
                    flow {
                        codeFlow.filterNotNull().map { code ->
                            authRepository.confirmAccount(code).fold(
                                onSuccess = {
                                    authRepository.login(pending.credentials.username, pending.credentials.password)
                                        .fold(
                                            onSuccess = {
                                                userRepository.save(Valid(it))
                                                LoggedIn(it)
                                            },
                                            onFailure = { CreateNewUser() }
                                        )
                                },
                                onFailure = { CreateNewUser() }
                            )
                        }.collect(this)
                    }
                },
            showsLogin.filterNotNull().map {
                if (it) {
                    AuthenticateUser
                } else {
                    CreateNewUser()
                }
            },
            userRepository.getUserStream()
                .filter { it == null }
                .flatMapLatest {
                    flow {
                        invitationCodeFlow.filterNotNull().map { code ->
                            CreateNewUser(code)
                        }.collect(this)
                    }
                },
            userRepository
                .getUserStream()
                .distinctUntilChangedBy {
                    when (it) {
                        is Valid -> it.user.id
                        is UserValue.Pending -> it
                        else -> null
                    }
                }.map { userValue ->
                    if (userValue == null) {
                        authRepository.deauthorize()
                        CreateNewUser(null)
                    } else {
                        when (userValue) {

                            is UserValue.Pending -> PendingConfirmation(userValue.credentials)
                            is Valid -> {
                                val kind = userValue.user.kind
                                if (kind is User.Kind.Remote) {
                                    authRepository.login(userValue.user.name, kind.password).fold(
                                        onSuccess = {
                                            preparedUser(it)
                                            LoggedIn(it)
                                        }, onFailure = {
                                            AuthenticateUser
                                        })
                                } else {
                                    preparedUser(userValue.user)
                                    LoggedIn(userValue.user)
                                }
                            }
                        }
                    }
                })
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading
            )

    private suspend fun preparedUser(user: User) {
        userRepository.save(Valid(user))
        authRepository.clientId = user.id
    }
}
