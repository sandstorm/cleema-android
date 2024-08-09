/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.registeruser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.AuthRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue
import de.cleema.android.core.models.IdentifiedImage
import de.cleema.android.di.InstantGenerator
import de.cleema.android.di.UUIDGenerator
import de.cleema.android.registeruser.RegisterUserUiState.Edit
import de.cleema.android.registeruser.RegisterUserUiState.Edit.Validation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.util.*
import javax.inject.Inject

sealed interface RegisterUserUiState {
    data class Edit(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val confirmation: String = "",
        val region: de.cleema.android.core.models.Region? = null,
        val acceptsSurveys: Boolean = false,
        val avatarId: IdentifiedImage? = null,
        val errorMessage: String? = null
    ) : RegisterUserUiState {
        enum class Validation { NAME, MAIL, PASSWORD, PASSWORD_CONFIRMATION, REGION }
    }

    object Saved : RegisterUserUiState
}

interface ValidationFormatter {
    fun format(validation: RegisterUserUiState.Edit.Validation): String
}

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase = ValidateEmailUseCase(),
    private val repository: AuthRepository,
    private val userRepository: UserRepository,
    private val validationFormatter: ValidationFormatter,
    @UUIDGenerator private val uuid: () -> UUID,
    @InstantGenerator private val instant: () -> Instant
) : ViewModel() {
    private var referralCode: String? = null

    private var _uiState: MutableStateFlow<RegisterUserUiState> =
        MutableStateFlow(Edit())
    val uiState: StateFlow<RegisterUserUiState> = _uiState

    fun enterName(name: String) = when (val state = _uiState.value) {
        is Edit -> {
            _uiState.value = state.copy(
                name = name,
                errorMessage = null
            )
        }
        else -> {}
    }

    fun enterPassword(password: String) = when (val it = _uiState.value) {
        is Edit -> {
            _uiState.value =
                it.copy(
                    password = password,
                    errorMessage = null
                )
        }
        else -> {}
    }

    fun enterMail(mail: String) = when (val it = _uiState.value) {
        is Edit -> {
            _uiState.value = it.copy(
                email = mail,
                errorMessage = null
            )
        }
        else -> {}
    }

    fun enterConfirmation(confirmation: String) = when (val it = _uiState.value) {
        is Edit -> {
            _uiState.value = it.copy(
                confirmation = confirmation,
                errorMessage = null
            )
        }
        else -> {}
    }

    fun toggleAcceptsSurveys() = when (val it = _uiState.value) {
        is Edit -> {
            _uiState.value = it.copy(acceptsSurveys = !it.acceptsSurveys)
        }
        else -> {}
    }

    fun enterRegion(region: de.cleema.android.core.models.Region) = when (val it = _uiState.value) {
        is Edit -> {
            _uiState.value = it.copy(
                region = region,
                errorMessage = null
            )
        }
        else -> {}
    }

    fun onClearErrorMessage() = when (val it = _uiState.value) {
        is Edit -> {
            _uiState.value = it.copy(
                errorMessage = null
            )
        }
        else -> {}
    }

    fun save() {
        val state = uiState.value as? Edit ?: return

        val validation = validation(state.name, state.password, state.confirmation, state.email, state.region)

        if (validation != null) {
            _uiState.value = state.copy(
                errorMessage = validationFormatter.format(validation)
            )
            return
        }
        val region = state.region ?: return
        viewModelScope.launch {
            repository.register(
                username = state.name,
                email = state.email,
                password = state.password,
                acceptsSurveys = state.acceptsSurveys,
                regionId = region.id,
                referralCode = referralCode
            ).onSuccess {
                userRepository.save(
                    UserValue.Pending(
                        de.cleema.android.core.models.Credentials(
                            state.name,
                            state.password,
                            state.email
                        )
                    )
                )
                referralCode?.let {
                    userRepository.followInvitation(it, true)
                }
            }.onFailure {
                _uiState.value = state.copy(errorMessage = it.message)
            }
        }
    }

    private fun validation(
        name: String,
        password: String,
        confirmation: String,
        email: String,
        region: de.cleema.android.core.models.Region?
    ): Edit.Validation? {
        if (name.isBlank() || name.length < 3) {
            return NAME
        }
        if (validateEmailUseCase(email).isNullOrBlank()) {
            return MAIL
        }
        if (password.length < 10) {
            return PASSWORD
        }
        if (password != confirmation) {
            return PASSWORD_CONFIRMATION
        }
        if (region == null) {
            return REGION
        }
        return null
    }

    fun setReferralCode(code: String) {
        referralCode = code
    }
}
