/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.ContentBackground

@Composable
fun MagazineItemContainer(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        ContentBackground(modifier = Modifier.fillMaxSize())
        MagazineItemContent(modifier)
    }
}


@Composable
fun MagazineItemContent(
    modifier: Modifier = Modifier,
    viewModel: MagazineItemViewModel = hiltViewModel()
) {
    Column(modifier = modifier) {
        when (val uiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
            is MagazineItemUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = uiState.reason)
            is MagazineItemUiState.Loading -> de.cleema.android.core.components.LoadingScreen(modifier)
            is MagazineItemUiState.Success -> Box {
                MagazineItemDetailCard(
                    uiState.magazineItem,
                    onFavClick = viewModel::favClicked,
                    didScrollToBottom = viewModel::markAsRead
                )
            }
        }
    }
}

