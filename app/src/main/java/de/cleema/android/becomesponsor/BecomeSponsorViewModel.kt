/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.becomesponsor.BecomeSponsorUiState.*
import de.cleema.android.core.data.SponsorRepository
import de.cleema.android.core.models.SponsorData
import de.cleema.android.core.models.SponsorPackage
import de.cleema.android.core.models.SponsorPackage.Companion.all
import de.cleema.android.core.models.SponsorPackage.Companion.fan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BecomeSponsorUiState {
    data class SelectPackage(
        val packages: List<SponsorPackage> = all,
        val selectedPackage: SponsorPackage = fan
    ) :
        BecomeSponsorUiState

    data class EnterData(
        val sponsorData: SponsorData = SponsorData(),
        val validationError: ValidationError? = null,
        val nextButtonEnabled: Boolean = false
    ) :
        BecomeSponsorUiState {
        enum class ValidationError { IBAN_INVALID }
    }

    data class Confirm(
        val selectedPackage: SponsorPackage,
        val sponsorData: SponsorData,
        val sepaConfirmed: Boolean = false
    ) : BecomeSponsorUiState

    object Thanks : BecomeSponsorUiState
    data class Error(val reason: String) : BecomeSponsorUiState
    object Loading : BecomeSponsorUiState
}

val BecomeSponsorUiState.isNextButtonEnabled: Boolean
    get() = when (this) {
        is EnterData -> this.nextButtonEnabled
        is Error, Loading, is SelectPackage, Thanks -> true
        is Confirm -> sepaConfirmed
    }

@HiltViewModel
class BecomeSponsorViewModel @Inject constructor(
    private val repository: SponsorRepository,
    private val validator: IBANValidator
) :
    ViewModel() {
    private var _packages: List<SponsorPackage> = listOf()
    private var _selectedPackage: SponsorPackage? = null
    private var _sponsorData = SponsorData()
    private var _contentFlow = MutableSharedFlow<BecomeSponsorUiState>()
    val uiState: StateFlow<BecomeSponsorUiState> =
        flow {
            emit(Loading)
            repository.getPackages().onSuccess { packages ->
                _packages = packages
                _selectedPackage = packages.first()
                emit(SelectPackage(packages, packages.first()))
            }.onFailure {
                emit(Error(it.localizedMessage ?: it.toString()))
            }

            _contentFlow.collect(this)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Loading
            )

    fun onNextClick() {
        when (val state = uiState.value) {
            is Confirm -> {
                if (!state.sepaConfirmed) return
                viewModelScope.launch {
                    _contentFlow.emit(repository.saveSponsorship(state.sponsorData, state.selectedPackage.type).fold(
                        onSuccess = { Thanks },
                        onFailure = { Error(it.localizedMessage ?: it.toString()) }
                    ))
                }
            }

            is EnterData -> {
                _selectedPackage?.let {
                    viewModelScope.launch {
                        _contentFlow.emit(Confirm(it, state.sponsorData))
                    }
                }
            }

            is SelectPackage -> viewModelScope.launch {
                _contentFlow.emit(enterData(_sponsorData))
            }
            Thanks, is Error, Loading -> return
        }
    }

    fun onSelectPackage(sponsorPackage: SponsorPackage) {
        val state = uiState.value
        _selectedPackage = sponsorPackage
        when (state) {
            is SelectPackage, is Confirm -> viewModelScope.launch {
                _contentFlow.emit(SelectPackage(_packages, sponsorPackage))
            }
            else -> return
        }
    }

    fun onEditSponsorData(sponsorData: SponsorData) {
        val state = uiState.value
        _sponsorData = sponsorData
        when (state) {
            is EnterData, is Confirm -> viewModelScope.launch {
                _contentFlow.emit(enterData(sponsorData))
            }
            else -> return
        }
    }

    private fun enterData(sponsorData: SponsorData): EnterData {
        val postalCode = sponsorData.postalCode.filter { it.isDigit() }.take(5)
        val validationResult = validate(sponsorData)
        return EnterData(
            sponsorData.copy(postalCode = postalCode),
            validationResult,
            nextButtonEnabled = validationResult == null && postalCode.length == 5 && sponsorData.isNotBlank
        )
    }

    private fun validate(sponsorData: SponsorData): EnterData.ValidationError? {
        if (sponsorData.iban.isBlank()) {
            return null
        }
        val isValid = validator.isValid(sponsorData.iban)
        return if (isValid) {
            null
        } else {
            EnterData.ValidationError.IBAN_INVALID
        }
    }

    fun onConfirmSepaClick() {
        val state = uiState.value
        if (state is Confirm) {
            viewModelScope.launch {
                _contentFlow.emit(state.copy(sepaConfirmed = !state.sepaConfirmed))
            }
        }
    }
}
