/*
 * Created by Kumpels and Friends on 2023-01-05
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.AuthRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue
import de.cleema.android.core.data.user
import de.cleema.android.core.models.Credentials
import de.cleema.android.core.models.User
import de.cleema.android.core.models.UserDetails
import de.cleema.android.profile.ProfileUiState.*
import de.cleema.android.profile.ProfileUiState.Edit.Validation.*
import de.cleema.android.registeruser.ValidateEmailUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Content(val user: User, val showsRemoveAlert: Boolean = false) : ProfileUiState
    data class Edit(
        val details: UserDetails,
        val user: User,
        val validation: Validation? = null,
        val saveErrorMessage: String? = null
    ) : ProfileUiState {
        enum class Validation { PASSWORD, PASSWORD_CONFIRMATION, NAME, PREVIOUS_PASSWORD, EMAIL_INVALID }
    }
}

val ProfileUiState.currentUser: User?
    get() = when (this) {
        is Content -> user
        is Edit -> user
        Loading -> null
    }

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val userDetails = MutableStateFlow<ProfileUiState.Edit?>(null)
    private val showsRemoveAlert = MutableStateFlow(false)
    val uiState: StateFlow<ProfileUiState> =
        combine(
            userRepository.getUserStream().map { it?.user }.filterNotNull(),
            userDetails,
            showsRemoveAlert
        ) { user, details, showsAlert ->
            details?.copy(user = user) ?: Content(user, showsAlert)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading
            )

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            authRepository.deauthorize()
        }
    }

    fun removeAccount() {
        showsRemoveAlert.value = true
    }

    fun edit(details: UserDetails) {
        val state = uiState.value
        if (state !is Edit) {
            return
        }
        userDetails.value = Edit(details, state.user, null)
    }

    fun startEditing() {
        when (val state = uiState.value) {
            is Content -> {
                userDetails.value = Edit(state.user.toDetails(), state.user, null)
            }

            else -> {}
        }
    }

    fun cancelEditing() {
        when (uiState.value) {
            is Edit -> {
                userDetails.value = null
            }
            is Content -> {
                showsRemoveAlert.value = false
            }
            else -> {}
        }
    }

    fun saveEditing() {
        when (val state = uiState.value) {
            is Edit -> {
                saveState(state)
            }
            is Content -> {
                showsRemoveAlert.value = false
                viewModelScope.launch {
                    userRepository.deleteUser()
                    authRepository.deauthorize()
                }
            }
            else -> return
        }
    }

    private fun saveState(state: Edit) {
        val validation = state.details.validate(state.user)

        if (validation == null) {
            viewModelScope.launch {
                when (state.user.kind) {
                    is User.Kind.Local -> {
                        val region = state.details.region
                        if (region != null && state.details.localUserId != null) {
                            registerWithDetails(state.details, region.id, state.user.id)
                                .onSuccess {
                                    userRepository.save(
                                        UserValue.Pending(
                                            Credentials(
                                                state.details.name,
                                                state.details.password,
                                                state.details.email
                                            )
                                        )
                                    )
                                    authRepository.clientId = null
                                    userDetails.value = null
                                }
                                .onFailure {
                                    userDetails.value = state.copy(saveErrorMessage = it.message)
                                }
                        } else {
                            updateUser(state.details)
                        }
                    }
                    is User.Kind.Remote -> {
                        updateUser(state.details)
                    }
                }
            }
        } else {
            userDetails.value = state.copy(validation = validation)
        }
    }

    private suspend fun updateUser(details: UserDetails) {
        userRepository.updateUser(details)
        userDetails.value = null
    }

    private suspend fun registerWithDetails(
        details: UserDetails,
        regionId: UUID,
        userId: UUID
    ) = authRepository.register(
        details.name,
        details.password,
        details.email,
        details.acceptsSurveys,
        regionId,
        details.avatar?.id,
        userId
    )


    fun convert() {
        when (val state = uiState.value) {
            is Content -> {
                if (state.user.kind is User.Kind.Local) {
                    userDetails.value = Edit(state.user.toDetails(true), state.user, null)
                }
            }

            else -> {}
        }
    }

    fun onClearErrorClick() {
        when (val state = uiState.value) {
            is Edit -> {
                if (state.user.kind is User.Kind.Local) {
                    userDetails.update {
                        it?.copy(saveErrorMessage = null)
                    }
                }
            }

            else -> {}
        }
    }
}

fun UserDetails.validate(user: User): Edit.Validation? {
    if (name.length < 3) {
        return NAME
    }
    if ((1..5).contains(password.length)) {
        return PASSWORD
    }
    if (passwordConfirmation != password) {
        return PASSWORD_CONFIRMATION
    }
    user.password?.let {
        if (password.isNotEmpty() && previousPassword != it) {
            return PREVIOUS_PASSWORD
        }
    }
    if ((user.kind is User.Kind.Remote || localUserId != null) && ValidateEmailUseCase().invoke(email) == null) {
        return EMAIL_INVALID
    }
    return null
}

val User.password: String?
    get() = when (val kind = this.kind) {
        User.Kind.Local -> null
        is User.Kind.Remote -> kind.password
    }

fun User.toDetails(shouldConvert: Boolean = false): UserDetails {
    return when (val kind = this.kind) {
        User.Kind.Local -> UserDetails(
            name,
            "",
            region,
            avatar,
            acceptsSurveys = acceptsSurveys,
            localUserId = id.takeIf { shouldConvert })
        is User.Kind.Remote -> UserDetails(name, kind.email, region, avatar, acceptsSurveys = acceptsSurveys)
    }
}
