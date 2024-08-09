package de.cleema.android.partnerchallenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.ChallengesRepository
import de.cleema.android.core.models.Challenge
import de.cleema.android.partnerchallenges.PartnerChallengesUiState.Content
import de.cleema.android.shared.RegionForCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface PartnerChallengesUiState {
    data class Content(val isLoading: Boolean, val challenges: List<Challenge>) :
        PartnerChallengesUiState

    data class Error(val reason: String) : PartnerChallengesUiState
}

@HiltViewModel
class PartnerChallengesViewModel @Inject constructor(
    repository: ChallengesRepository,
    private val regionForCurrentUserUseCase: RegionForCurrentUserUseCase
) :
    ViewModel() {
    private var selectedRegion = MutableStateFlow<UUID?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PartnerChallengesUiState> =
        selectedRegion
            .onStart { emit(regionForCurrentUserUseCase().id) }
            .filterNotNull()
            .flatMapLatest { uuid ->
                repository.getChallengesStream(uuid).map { it ->
                    it.fold(
                        { Content(false, it) },
                        {
                            PartnerChallengesUiState.Error(reason = it.localizedMessage ?: "Unknown error")
                        }
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Content(true, listOf())
            )

    fun setRegion(regionId: UUID) {
        selectedRegion.value = regionId
    }
}
