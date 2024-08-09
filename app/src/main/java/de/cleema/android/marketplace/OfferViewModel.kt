/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.MarketplaceRepository
import de.cleema.android.core.models.MarketItem
import de.cleema.android.di.LocationOpener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed interface OfferUiState {
    data class Success(val offer: de.cleema.android.core.models.MarketItem) : OfferUiState
    data class Error(val reason: String) : OfferUiState
    object Loading : OfferUiState
}

val OfferUiState.currentOffer: de.cleema.android.core.models.MarketItem?
    get() = when (this) {
        is OfferUiState.Success -> offer
        else -> null
    }

fun Result<de.cleema.android.core.models.MarketItem>.toUiState(code: String?): OfferUiState = fold(
    onSuccess = {
        OfferUiState.Success(
            if (code != null) {
                it.copy(voucherRedemption = MarketItem.VoucherRedemption.Redeemed(code))
            } else {
                it
            }
        )
    },
    onFailure = { OfferUiState.Error(reason = it.localizedMessage ?: "Unknown error") }
)

@HiltViewModel
class OfferViewModel @Inject constructor(
    private val repository: MarketplaceRepository,
    private val mapOpener: LocationOpener,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val idFlow = savedStateHandle.getStateFlow("marketplaceId", "")

    @OptIn(ExperimentalCoroutinesApi::class)
    var uiState: StateFlow<OfferUiState> =
        idFlow
            .map {
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            .filterNotNull()
            .flatMapLatest {
                repository.getVoucherStream(it).combine(
                    repository.getOfferStream(it)
                ) { code, offer ->
                    offer.toUiState(code)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = OfferUiState.Loading
            )

    fun onRequestClicked() {
        uiState.value.currentOffer?.let {
            viewModelScope.launch {
                // TODO: handle error
                repository.requestVoucher(it.id)
            }
        }
    }

    // Currently not used
    fun onRedeemClick() {
        uiState.value.currentOffer?.let {
            val redemption = it.voucherRedemption
            if (redemption is MarketItem.VoucherRedemption.Redeemed) {
                viewModelScope.launch {
                    repository.redeemVoucher(it.id, redemption.code)
                }
            }
        }
    }

    fun onMapClicked() {
        uiState.value.currentOffer?.let {
            it.location?.let { location ->
                viewModelScope.launch {
                    mapOpener.openLocation(location)
                }
            }
        }
    }
}
