/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.inviteusers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import java.util.*


@Composable
fun InviteUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: InviteUsersViewModel = hiltViewModel(),
    onSelectionChanged: (Set<UUID>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    InviteUsersScreen(
        state = uiState,
        modifier = modifier,
        onUserClicked = { viewModel.selectUser(it.id) },
        onSelectionChanged = onSelectionChanged
    )
}

@Composable
internal fun InviteUsersScreen(
    state: InviteUsersUiState,
    modifier: Modifier = Modifier,
    onUserClicked: (de.cleema.android.core.models.SocialUser) -> Unit,
    onSelectionChanged: (Set<UUID>) -> Unit
) {
    Column(
        modifier = modifier
            .background(Accent)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.invite_friends_title),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.invite_friends_hint),
            style = MaterialTheme.typography.bodyLarge
        )

        when (state) {
            is InviteUsersUiState.Content -> {
                LaunchedEffect(state.selection) {
                    onSelectionChanged(state.selection)
                }
                LazyColumn(
                    modifier = Modifier.background(Color.White, shape = MaterialTheme.shapes.medium),
                    horizontalAlignment = CenterHorizontally
                ) {
                    itemsIndexed(state.followers) { idx, user ->
                        SocialUserCard(
                            user.avatar,
                            user.username,
                            state.selection.contains(user.id),
                            idx < state.followers.count() - 1,
                            Modifier.fillParentMaxWidth(),
                            onClick = { onUserClicked(user) }
                        )
                    }
                }
            }

            is InviteUsersUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
            InviteUsersUiState.Loading -> de.cleema.android.core.components.LoadingScreen()
        }
    }
}

@Preview("Invite users list", widthDp = 320)
@Composable
fun InviteUsersScreenPreview() {
    CleemaTheme {
        InviteUsersScreen(
            state = InviteUsersUiState.Content(
                listOf(
                    de.cleema.android.core.models.SocialUser(username = "Rico"),
                    de.cleema.android.core.models.SocialUser(username = "Gunnar"),
                    de.cleema.android.core.models.SocialUser(username = "Markus")
                ),
                selection = setOf()
            ),
            modifier = Modifier
                .fillMaxWidth(),
            onUserClicked = {},
            onSelectionChanged = {}
        )
    }
}

