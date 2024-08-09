/*
 * Created by Kumpels and Friends on 2023-01-05
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Recessed
import de.cleema.android.profile.ProfileUiState.*
import kotlinx.datetime.Instant
import java.util.*

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    profileState: ProfileUiState,
    favoritesState: FavoritesUiState,
    trophiesState: TrophiesUiState,
    onLogoutClick: () -> Unit,
    onRemoveAccountClick: (Boolean) -> Unit,
    onInviteClick: () -> Unit,
    onProjectClick: (UUID) -> Unit,
    onMagazineItemClick: (UUID) -> Unit,
    onTrophyClick: (UUID) -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingsClick: () -> Unit,
    onEditUser: (de.cleema.android.core.models.UserDetails) -> Unit,
    onConvertClick: () -> Unit,
    onDismissAlertClick: () -> Unit,
    onClearErrorClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyColumn {
            item {
                when (profileState) {
                    is Content -> {
                        ProfileUserView(
                            profileState.user,
                            Modifier.padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
                            onInviteClick,
                            onFollowersClick,
                            onFollowingsClick,
                            onConvertClick = onConvertClick
                        )

                        if (profileState.showsRemoveAlert) {
                            AlertDialog(
                                modifier = modifier,
                                onDismissRequest = onDismissAlertClick,
                                text = {
                                    Text(
                                        stringResource(R.string.profile_remove_account_label)
                                    )
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = { onRemoveAccountClick(true) }
                                    ) {
                                        Text(stringResource(R.string.alert_delete_button_title), color = Color.Red)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = onDismissAlertClick) {
                                        Text(stringResource(R.string.alert_dismiss_button_title))
                                    }
                                }
                            )
                        }
                    }

                    is Edit -> {
                        EditProfileDetails(
                            state = profileState,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
                            onEdit = onEditUser,
                            onClearErrorClick = onClearErrorClick
                        )
                    }

                    Loading -> de.cleema.android.core.components.LoadingScreen()
                }
            }

            favoritesContent(
                state = favoritesState,
                enabled = profileState.isEnabled,
                onProjectClick = onProjectClick,
                onMagazineItemClick = onMagazineItemClick
            )

            item {
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillParentMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(DefaultText)
                        .padding(20.dp)
                ) {
                    TrophiesView(trophiesState, enabled = profileState.isEnabled, onTrophyClick = onTrophyClick)

                    Spacer(modifier = Modifier.weight(1f, false))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        profileState.currentUser?.let { user ->
                            if (user.kind is de.cleema.android.core.models.User.Kind.Remote) {
                                TextButton(onClick = onLogoutClick, enabled = profileState.isEnabled) {
                                    Text(
                                        stringResource(R.string.profile_logout),
                                        color = if (profileState.isEnabled) Recessed else Recessed.copy(alpha = 0.2f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        TextButton(onClick = { onRemoveAccountClick(false) }, enabled = profileState.isEnabled) {
                            Text(
                                stringResource(R.string.profile_remove_account),
                                color = if (profileState.isEnabled) Recessed else Recessed.copy(alpha = 0.2f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .background(DefaultText)
                .fillMaxHeight()
                .fillMaxWidth()
        )
    }
}

val ProfileUiState.isEnabled: Boolean
    get() = when (this) {
        is Content -> true
        is Edit -> false
        Loading -> false
    }

@Preview(widthDp = 360)
@Composable
fun ProfileContentPreview() {
    val user = de.cleema.android.core.models.User(
        name = "Clara Cleema",
        region = de.cleema.android.core.models.Region.LEIPZIG,
        joinDate = Instant.DISTANT_FUTURE,
        kind = de.cleema.android.core.models.User.Kind.Remote("", "foo@bar.de", ""),
        followerCount = 8,
        followingCount = 7,
        acceptsSurveys = true,
        isSupporter = false
    )
    CleemaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Accent)
        ) {
            ProfileContent(
                profileState = Content(user),
                favoritesState = FavoritesUiState.Loading,
                trophiesState = TrophiesUiState.Content(
                    listOf(
                        de.cleema.android.core.models.Trophy.empty,
                        de.cleema.android.core.models.Trophy.empty,
                        de.cleema.android.core.models.Trophy.empty
                    )
                ),
                onLogoutClick = {},
                onRemoveAccountClick = {},
                onInviteClick = {},
                onProjectClick = {},
                onMagazineItemClick = {},
                onTrophyClick = {},
                onFollowersClick = {},
                onFollowingsClick = {},
                onEditUser = {},
                onConvertClick = {},
                onDismissAlertClick = {},
                onClearErrorClick = {}
            )
        }
    }
}
