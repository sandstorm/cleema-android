/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomepartner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.becomepartner.BecomePartnerUiState.*
import de.cleema.android.core.data.PartnerRepository
import de.cleema.android.core.models.PartnerPackage.Companion.all
import de.cleema.android.core.models.PartnerPackage.Companion.starter
import de.cleema.android.di.MailOpener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BecomePartnerUiState {
    data class SelectPackage(
        val packages: List<de.cleema.android.core.models.PartnerPackage> = all,
        val selectedPackage: de.cleema.android.core.models.PartnerPackage = starter
    ) :
        BecomePartnerUiState

    data class Error(val reason: String) : BecomePartnerUiState
    object Loading : BecomePartnerUiState
}

@HiltViewModel
class BecomePartnerViewModel @Inject constructor(
    private val repository: PartnerRepository,
    private val mailOpener: MailOpener,
) :
    ViewModel() {
    private var _packages: List<de.cleema.android.core.models.PartnerPackage> = listOf()
    private var _selectedPackage: de.cleema.android.core.models.PartnerPackage? = null
    private var _contentFlow = MutableSharedFlow<BecomePartnerUiState>()
    val uiState: StateFlow<BecomePartnerUiState> =
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

    fun onSelectPackage(partnerPackage: de.cleema.android.core.models.PartnerPackage) {
        val state = uiState.value
        _selectedPackage = partnerPackage
        when (state) {
            is SelectPackage -> viewModelScope.launch {
                _contentFlow.emit(SelectPackage(_packages, partnerPackage))
            }
            else -> return
        }
    }

    fun onContactClick() {
        _selectedPackage?.let { selectedPackage ->
            viewModelScope.launch {
                mailOpener.openMail(
                    recipient = "partner@cleema.app",
                    subject = "Partnerschaftsanfrage",
                    body = "Gewähltes Paket: ${selectedPackage.title}\n\n"
                )
            }
        }
    }
}
