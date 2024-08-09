/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.profile.FavoritesUiState.Favorite
import java.util.*

fun LazyListScope.favoritesContent(
    state: FavoritesUiState,
    horizontalPadding: Dp = 20.dp,
    enabled: Boolean = true,
    onProjectClick: (UUID) -> Unit = {},
    onMagazineItemClick: (UUID) -> Unit = {}
) {
    item {
        Text(
            stringResource(R.string.profile_favorites_title),
            Modifier.padding(horizontal = horizontalPadding),
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) DefaultText else DefaultText.copy(alpha = 0.4f)
        )
    }

    when (state) {
        is FavoritesUiState.Content -> if (state.favorites.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.profile_favorites_empty_list),
                    Modifier.padding(horizontal = horizontalPadding),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) DefaultText else DefaultText.copy(alpha = 0.4f)
                )
            }
        } else {
            items(state.favorites) { favorite ->
                FavoriteButton(
                    title = favorite.title,
                    iconRes = favorite.icon,
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding)
                        .alpha(if (enabled) 1f else 0.55f),
                    enabled = enabled
                ) {
                    when (favorite) {
                        is Favorite.ProjectFavorite -> onProjectClick(favorite.project.id)
                        is Favorite.MagazineItemFavorite -> onMagazineItemClick(favorite.magazineItem.id)
                    }
                }
            }
        }
        is FavoritesUiState.Error ->
            item {
                de.cleema.android.core.components.ErrorScreen(
                    message = state.reason
                )
            }
        FavoritesUiState.Loading ->
            item {
                de.cleema.android.core.components.LoadingScreen()
            }
    }
}

val Favorite.title: String
    get() = when (this) {
        is Favorite.MagazineItemFavorite -> magazineItem.title
        is Favorite.ProjectFavorite -> project.title
    }
val Favorite.icon: Int
    get() = when (this) {
        is Favorite.MagazineItemFavorite -> R.drawable.news_tab_icon
        is Favorite.ProjectFavorite -> R.drawable.projects_tab_icon
    }
