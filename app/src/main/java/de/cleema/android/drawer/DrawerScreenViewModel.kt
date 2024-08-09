/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.drawer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.InfoRepository
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.drawer.DrawerScreenUiState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed interface DrawerScreenUiState {
    val route: DrawerRoute

    data class Success(val content: String, override val route: DrawerRoute) : DrawerScreenUiState
    data class Error(val reason: String, override val route: DrawerRoute) : DrawerScreenUiState
    data class Loading(override val route: DrawerRoute = DrawerRoute.ABOUT) : DrawerScreenUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DrawerScreenViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var uiState: StateFlow<DrawerScreenUiState> =
        savedStateHandle.getStateFlow(
            drawerRouteArg,
            ""
        ).mapNotNull {
            kotlin.runCatching { DrawerRoute.valueOf(it) }.getOrNull()
        }.mapLatest { route ->
            infoRepository.getContent(route).fold(onSuccess = { value ->
                Success(value, route)
            }, onFailure = {
                Error(it.localizedMessage ?: it.toString(), route)
            })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Loading(kotlin.runCatching {
                DrawerRoute.valueOf(
                    savedStateHandle.get<String>(drawerRouteArg) ?: ""
                )
            }.getOrNull() ?: DrawerRoute.ABOUT)
        )
}
