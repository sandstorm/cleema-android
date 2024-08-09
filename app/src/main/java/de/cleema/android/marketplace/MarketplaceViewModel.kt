/*
 * Created by Kumpels and Friends on 2022-12-02
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.MarketplaceRepository
import de.cleema.android.shared.RegionForCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface MarketplaceUiState {
    data class Success(val offers: List<de.cleema.android.core.models.MarketItem>) : MarketplaceUiState
    data class Error(val reason: String) : MarketplaceUiState
    object Loading : MarketplaceUiState

}

fun Result<List<de.cleema.android.core.models.MarketItem>>.toUiState(): MarketplaceUiState = fold(
    onSuccess = MarketplaceUiState::Success,
    onFailure = { MarketplaceUiState.Error(reason = it.localizedMessage ?: "Unknown error") }
)

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val repository: MarketplaceRepository,
    private val regionForCurrentUserUseCase: RegionForCurrentUserUseCase
) : ViewModel() {
    private var selectedRegion = MutableStateFlow<UUID?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MarketplaceUiState> =
        selectedRegion
            .onStart { emit(regionForCurrentUserUseCase().id) }
            .filterNotNull()
            .flatMapLatest {
                repository.getMarketplaceStream(it)
            }
            .map(Result<List<de.cleema.android.core.models.MarketItem>>::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MarketplaceUiState.Loading
            )

    fun setRegion(regionId: UUID) {
        selectedRegion.value = regionId
    }
}
