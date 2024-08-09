/*
 * Created by Kumpels and Friends on 2023-01-05
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.social.InviteLinkViewModel
import java.util.*


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    trophiesViewModel: TrophiesViewModel = hiltViewModel(),
    inviteViewModel: InviteLinkViewModel = hiltViewModel(),
    onProjectClick: (UUID) -> Unit,
    onMagazineItemClick: (UUID) -> Unit,
    onTrophyClick: (UUID) -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingsClick: () -> Unit
) {
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    // TODO: move to ProfileUserScreen, do not remove, as this flow must be collected
    val inviteState by inviteViewModel.uiState.collectAsStateWithLifecycle()
    val favoritesState by favoritesViewModel.uiState.collectAsStateWithLifecycle()
    val trophiesState by trophiesViewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        modifier = modifier,
        profileState = profileState,
        favoritesState = favoritesState,
        trophiesState = trophiesState,
        onLogoutClick = profileViewModel::logout,
        onRemoveAccountClick = { isConfirmed ->
            if (isConfirmed) {
                profileViewModel.saveEditing()
            } else {
                profileViewModel.removeAccount()
            }
        },
        onInviteClick = inviteViewModel::inviteUsersClicked,
        onProjectClick = onProjectClick,
        onMagazineItemClick = onMagazineItemClick,
        onTrophyClick = onTrophyClick,
        onFollowersClick = onFollowersClick,
        onFollowingsClick = onFollowingsClick,
        onEditUser = profileViewModel::edit,
        onConvertClick = profileViewModel::convert,
        onDismissAlertClick = profileViewModel::cancelEditing,
        onClearErrorClick = profileViewModel::onClearErrorClick
    )
}
