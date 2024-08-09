/*
 * Created by Kumpels and Friends on 2022-12-19
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.MagazineRepository
import de.cleema.android.core.data.ProjectsRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

sealed interface FavoritesUiState {
    object Loading : FavoritesUiState
    data class Content(val favorites: List<Favorite>) : FavoritesUiState
    data class Error(val reason: String) : FavoritesUiState
    sealed interface Favorite {
        data class ProjectFavorite(val project: de.cleema.android.core.models.Project) : Favorite
        data class MagazineItemFavorite(val magazineItem: de.cleema.android.core.models.MagazineItem) : Favorite
    }
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    val projectsRepository: ProjectsRepository,
    val magazineRepository: MagazineRepository
) :
    ViewModel() {
    val uiState: StateFlow<FavoritesUiState> =
        projectsRepository.getFavedProjectsStream()
            .combine(magazineRepository.getFavedMagazineItemsStream()) { projectsResult, magazineItemsResult ->
                val projectsFavs = projectsResult.fold(
                    onSuccess = { Result.success(it.map(FavoritesUiState.Favorite::ProjectFavorite)) },
                    onFailure = { Result.failure(it) }
                )
                val magazineItemFavs = magazineItemsResult.fold(
                    onSuccess = { Result.success(it.map(FavoritesUiState.Favorite::MagazineItemFavorite)) },
                    onFailure = { Result.failure(it) }
                )

                if (projectsFavs.isFailure && magazineItemFavs.isFailure) {
                    // TODO: Improve error handling
                    return@combine Result.failure(projectsFavs.exceptionOrNull()!!)
                }

                val favs: MutableList<FavoritesUiState.Favorite> = mutableListOf()
                projectsFavs.getOrNull()?.let {
                    favs.addAll(it)
                }
                magazineItemFavs.getOrNull()?.let {
                    favs.addAll(it)
                }
                return@combine Result.success(favs.toList())
            }
            .map(Result<List<FavoritesUiState.Favorite>>::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = FavoritesUiState.Loading
            )
}

fun Result<List<FavoritesUiState.Favorite>>.toUiState(): FavoritesUiState = fold(
    onSuccess = FavoritesUiState::Content,
    onFailure = { FavoritesUiState.Error(reason = it.localizedMessage ?: "Unknown error") }
)
