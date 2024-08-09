/*
 * Created by Kumpels and Friends on 2022-12-08
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.ContentBackground
import de.cleema.android.regions.RegionsList
import java.util.*


@Composable
fun MarketplaceScreen(
    modifier: Modifier = Modifier,
    viewModel: MarketplaceViewModel = hiltViewModel(),
    onOfferClick: (UUID) -> Unit
) {
    Box(modifier = modifier) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ContentBackground(modifier = Modifier.fillMaxSize())
        MarketplaceContent(uiState, modifier, viewModel::setRegion, onOfferClick)
    }
}

@Composable
fun MarketplaceContent(
    state: MarketplaceUiState,
    modifier: Modifier = Modifier,
    onSelectRegion: (UUID) -> Unit = {},
    onOfferClick: (UUID) -> Unit = {},
) {
    when (state) {
        is MarketplaceUiState.Success -> Column(modifier = Modifier.fillMaxWidth()) {
            RegionsList(modifier = Modifier.padding(horizontal = 20.dp)) { onSelectRegion(it.id) }
            MarketPlaceItemList(state.offers, modifier, onOfferClick)
        }
        is MarketplaceUiState.Error -> de.cleema.android.core.components.ErrorScreen(
            state.reason,
            modifier = Modifier
                .padding(horizontal = 20.dp)
        )
        is MarketplaceUiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
