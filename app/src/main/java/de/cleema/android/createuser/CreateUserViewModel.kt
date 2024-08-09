/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.R
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.createuser.CreateUserUiState.*
import de.cleema.android.di.InstantGenerator
import de.cleema.android.di.UUIDGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.util.*
import javax.inject.Inject

data class EditUser(
    val name: String = "", val region: de.cleema.android.core.models.Region? = null, val acceptsSurveys: Boolean = false
)

sealed interface CreateUserUiState {
    data class Edit(
        val user: EditUser = EditUser(), val errorMessage: String? = null
    ) : CreateUserUiState

    object Saved : CreateUserUiState
    data class Error(val reason: String) : CreateUserUiState
}

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val repository: UserRepository,
    @UUIDGenerator private val uuid: () -> UUID,
    @InstantGenerator private val instant: () -> Instant
) : ViewModel() {
    private var _uiState: MutableStateFlow<CreateUserUiState> =
        MutableStateFlow(Edit())

    //    private var editFlow: MutableStateFlow<EditUser> =
//        MutableStateFlow(EditUser())
    private var saveState = MutableSharedFlow<de.cleema.android.core.models.User>()
    val uiState: StateFlow<CreateUserUiState> =
        merge(
            _uiState,
            repository.getUserStream().mapNotNull { user ->
                if (user == null) {
                    Edit()
                } else {
                    null
                }
            },
            saveState.map {
                try {
                    repository.save(Valid(it))
                    Saved
                } catch (e: Throwable) {
                    Error(reason = e.toString())
                }
            }
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Edit()
        )

    fun update(editUser: EditUser) = when (val it = uiState.value) {
        is Edit -> {
            _uiState.value =
                it.copy(
                    user = editUser,
                    errorMessage = null
                )
        }
        else -> {}
    }

    fun onClearErrorMessage() = when (val it = uiState.value) {
        is Edit -> {
            _uiState.value = it.copy(
                errorMessage = null
            )
        }
        else -> {}
    }

    fun save() {
        val state = uiState.value
        if (state is Edit) {
            if (state.user.name.isBlank()) {
                _uiState.value = state.copy(
                    errorMessage = "R.string.name_validation"
                )
                return
            }
            if (state.user.region == null) {
                _uiState.value = state.copy(
                    errorMessage = "R.string.region_validation"
                )
                return
            }

            state.user.region.let {
                viewModelScope.launch {
                    saveState.emit(
                        de.cleema.android.core.models.User(
                            id = uuid(),
                            name = state.user.name.trim(),
                            region = it,
                            joinDate = instant(),
                            kind = de.cleema.android.core.models.User.Kind.Local,
                            followerCount = 0,
                            followingCount = 0,
                            acceptsSurveys = state.user.acceptsSurveys,
                            isSupporter = false
                        )
                    )
                }
            }
        }
    }
}
