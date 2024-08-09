/*
 * Created by Kumpels and Friends on 2023-01-03
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.inviteusers.SocialUserCard
import java.util.*


@Composable
fun SocialUserListScreen(
    showsFollowers: Boolean,
    modifier: Modifier = Modifier,
    viewModel: SocialGraphViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SocialUserListScreen(
        modifier = modifier,
        state = uiState,
        showsFollowers = showsFollowers,
        onRemoveUserClick = viewModel::onRemoveUserClick,
        onConfirmAlertClick = viewModel::confirmUnfollowing,
        onDismissAlertClick = viewModel::dismissAlert,
    )
}

@Composable
fun SocialUserListScreen(
    modifier: Modifier = Modifier,
    state: SocialGraphUiState,
    showsFollowers: Boolean,
    onRemoveUserClick: (UUID) -> Unit,
    onConfirmAlertClick: () -> Unit,
    onDismissAlertClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        when (state) {
            is SocialGraphUiState.Content -> SocialUserListContent(
                users = if (showsFollowers) state.followers else state.following,
                removedUserName = state.alertItem?.user?.username,
                onRemoveUserClick = onRemoveUserClick,
                onConfirmAlertClick = onConfirmAlertClick,
                onDismissAlertClick = onDismissAlertClick
            )
            is SocialGraphUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
            SocialGraphUiState.Loading -> de.cleema.android.core.components.LoadingScreen()
        }
    }
}

@Composable
fun SocialUserListContent(
    users: List<de.cleema.android.core.models.SocialGraphItem>,
    removedUserName: String?,
    modifier: Modifier = Modifier,
    onRemoveUserClick: (UUID) -> Unit,
    onConfirmAlertClick: () -> Unit,
    onDismissAlertClick: () -> Unit
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users) { socialGraphItem ->
            Card(Modifier.fillParentMaxWidth()) {
                SocialUserCard(
                    socialGraphItem.user.avatar,
                    socialGraphItem.user.username,
                    checked = false,
                    showsDivider = false,
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = { onRemoveUserClick(socialGraphItem.id) }
                )
            }
        }
    }

    removedUserName?.let {
        StopFollowUserAlert(
            userName = it,
            onRemoveClick = onConfirmAlertClick,
            onDismissClick = onDismissAlertClick
        )
    }
}

@Preview("Social user list", widthDp = 320)
@Composable
fun SocialUserListContentPreview() {
    CleemaTheme {
        SocialUserListContent(
            users = listOf(
                de.cleema.android.core.models.SocialGraphItem(user = de.cleema.android.core.models.SocialUser(username = "Clara Cleema")),
                de.cleema.android.core.models.SocialGraphItem(user = de.cleema.android.core.models.SocialUser(username = "Carl Klammer"))
            ),
            removedUserName = null,
            onRemoveUserClick = {},
            onConfirmAlertClick = { /*TODO*/ }) {
        }
    }
}
