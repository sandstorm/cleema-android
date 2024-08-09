/*
 * Created by Kumpels and Friends on 2022-11-17
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.ContentBackground
import java.util.*


@Composable
fun MagazineScreen(
    modifier: Modifier = Modifier,
    viewModel: MagazineViewModel = hiltViewModel(),
    onNavigateToItem: (UUID) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ContentBackground(modifier = Modifier.fillMaxSize())
    MagazineContent(uiState, modifier, onNavigateToItem, viewModel::favClicked)
}

@Composable
fun MagazineContent(
    state: MagazineUiState,
    modifier: Modifier = Modifier,
    onMoreClick: (UUID) -> Unit = {},
    onFavClick: (UUID) -> Unit = {},
) {
    when (state) {
        is MagazineUiState.Success -> MagazineItemsList(
            state.magazineItems,
            modifier,
            onMoreClick,
            onFavClick
        )
        is MagazineUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
        is MagazineUiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
