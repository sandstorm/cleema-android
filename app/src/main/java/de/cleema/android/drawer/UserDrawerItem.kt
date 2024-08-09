/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.profile.UserAvatar

@Composable
fun UserDrawerItem(
    modifier: Modifier = Modifier,
    viewModel: UserDrawerItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is UserDrawerItemUiState.Content -> UserDrawerItem(user = state.user, modifier = modifier)
        UserDrawerItemUiState.Loading -> return
    }
}

@Composable
fun UserDrawerItem(
    user: de.cleema.android.core.models.User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        UserAvatar(user = user, avatarDefaultColor = Accent, modifier = Modifier.size(80.dp))

        Text(user.name, color = Accent, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun UserDrawerItemPreview() {
    CleemaTheme {
        UserDrawerItem(
            user = de.cleema.android.core.models.User(
                name = "Hansbernd",
                region = de.cleema.android.core.models.Region.LEIPZIG,
                avatar = de.cleema.android.core.models.IdentifiedImage(
                    image = de.cleema.android.core.models.RemoteImage.of(
                        "https://picsum.photos/200/300",
                        de.cleema.android.core.models.Size(200f, 300f)
                    )
                )
            )
        )
    }
}
