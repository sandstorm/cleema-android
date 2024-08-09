/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.selectavatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAvatarList(
    modifier: Modifier = Modifier,
    selection: UUID? = null,
    viewModel: SelectAvatarViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSelectAvatar: (de.cleema.android.core.models.IdentifiedImage) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Accent,
                navigationIconContentColor = DefaultText
            ),
            navigationIcon = {
                TextButton(onClick = onDismiss) {
                    Text(
                        stringResource(R.string.profile_edit_cancel_button_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
            },
            title = {},
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            actions = {
                TextButton(onClick = {
                    val state = uiState
                    if (state is SelectAvatarUiState.Content) {
                        state.selection?.let { onSelectAvatar(it) }
                        onDismiss()
                    }
                }, enabled = uiState.chooseButtonEnabled) {
                    Text(
                        stringResource(R.string.profile_avatar_choose),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
                }
            }
        )
    }) {
        LaunchedEffect(key1 = selection) {
            viewModel.select(selection)
        }
        SelectAvatarList(
            state = uiState,
            modifier = modifier
                .padding(it)
                .padding(bottom = 20.dp),
            onClick = viewModel::select
        )
    }
}

val SelectAvatarUiState.chooseButtonEnabled: Boolean
    get() = when (this) {
        is SelectAvatarUiState.Content -> this.selection != null
        is SelectAvatarUiState.Error -> false
        SelectAvatarUiState.Loading -> false
    }

@Composable
fun SelectAvatarList(
    state: SelectAvatarUiState,
    modifier: Modifier = Modifier,
    onClick: (UUID) -> Unit
) {
    when (state) {
        is SelectAvatarUiState.Content -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = modifier.background(Accent)
            ) {
                items(state.avatars) { avatar ->
                    de.cleema.android.core.components.RemoteImageView(
                        remoteImage = avatar.image, contentScale = ContentScale.Crop, modifier = Modifier
                            .size(104.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .border(
                                width = if (state.selection?.id == avatar.id) 4.dp else 0.dp,
                                color = if (state.selection?.id == avatar.id) Action else Color.Unspecified,
                                shape = CircleShape
                            )
                            .clickable(
                                onClickLabel = avatar.image.url,
                                role = Role.Image
                            ) { onClick(avatar.id) }
                    )
                }
            }
        }

        is SelectAvatarUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason, modifier)
        SelectAvatarUiState.Loading -> de.cleema.android.core.components.LoadingScreen(modifier)
    }

}

@Preview("Select avatar list", widthDp = 320)
@Composable
fun SelectAvatarListPreview() {
    CleemaTheme {
        SelectAvatarList(
            state = SelectAvatarUiState.Content(
                (1..10).map {
                    de.cleema.android.core.models.IdentifiedImage(
                        image = de.cleema.android.core.models.RemoteImage.of(
                            "https://loremflickr.com/320/320?random=${Random.nextInt(100, 10000)}",
                            de.cleema.android.core.models.Size(320f, 320f)
                        )
                    )
                }
            ),
            onClick = {}
        )
    }
}
