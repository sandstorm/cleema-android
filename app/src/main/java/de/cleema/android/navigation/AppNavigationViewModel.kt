package de.cleema.android.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.core.models.User
import de.cleema.android.navigation.AppNavigationUiState.Destination.Drawer
import de.cleema.android.navigation.AppNavigationUiState.Destination.Info
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class InfoContent(val route: DrawerRoute, val enabled: Boolean = true)

data class AppNavigationUiState(
    val screen: AppScreens = AppScreens.Dashboard,
    val infoDrawerContent: List<InfoContent> = listOf(),
    val destination: Destination? = null
) {
    sealed interface Destination {
        object Drawer : Destination
        data class Info(val route: DrawerRoute) : Destination
        object Profile : Destination
    }
}

@HiltViewModel
class AppNavigationViewModel @Inject constructor(val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(AppNavigationUiState(infoDrawerContent = listOf()))

    val uiState: StateFlow<AppNavigationUiState> = _state.combine(repository.getUserStream()) { state, user ->
        state.copy(
            screen = if (user == null) AppScreens.Dashboard else state.screen,
            infoDrawerContent = infoContent(user)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppNavigationUiState()
    )

    private fun infoContent(user: UserValue?): List<InfoContent> = when (user) {
        is UserValue.Pending -> listOf()
        is UserValue.Valid -> {
            DrawerRoute.values()
                .map {
                    InfoContent(
                        it, /*if (it == DrawerRoute.SPONSORSHIP) user.user.kind is User.Kind.Remote else*/ true
                    )
                }
        }
        null -> listOf()
    }

    fun openNavigationMenu() {
        _state.update {
            it.copy(destination = Drawer)
        }
    }

    fun select(route: DrawerRoute) {
        _state.update {
            it.copy(destination = Info(route))
        }
    }

    fun close() {
        _state.update { it.copy(destination = null) }
    }

    fun selectScreen(screen: AppScreens) {
        _state.update {
            it.copy(screen = screen, destination = null)
        }
    }

    fun openProfile() {
        _state.update {
            it.copy(destination = AppNavigationUiState.Destination.Profile)
        }
    }
}

