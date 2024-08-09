/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.components.map.MapView
import de.cleema.android.core.models.Location
import de.cleema.android.core.models.MarketItem.VoucherRedemption.Exhausted
import de.cleema.android.core.styling.ContentBackground


@Composable
fun OfferScreen(
    modifier: Modifier = Modifier,
    viewModel: OfferViewModel = hiltViewModel(),
) {
    ContentBackground(modifier = Modifier.fillMaxSize())
    Column(modifier = modifier) {
        when (val uiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
            is OfferUiState.Error -> {
                de.cleema.android.core.components.ErrorScreen(
                    stringResource(
                        R.string.marketplace_offer_error_format_string,
                        uiState.reason
                    )
                )
            }
            is OfferUiState.Loading -> Box(
                modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            is OfferUiState.Success -> Box {
                OfferCard(
                    uiState.offer,
                    onRequestClick = viewModel::onRequestClicked,
                    // onRedeemClick = viewModel::onRedeemClick,
                    onLocationClick = {
                        viewModel.onMapClicked()
                    })
            }
        }
    }
}

@Composable
fun OfferCard(
    offer: de.cleema.android.core.models.MarketItem,
    modifier: Modifier = Modifier,
    onRequestClick: () -> Unit,
    // onRedeemClick: () -> Unit,
    onLocationClick: (Location) -> Unit
) {
    Card(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column {
            de.cleema.android.core.components.RemoteImageView(
                remoteImage = offer.image,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = offer.subTitle,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (offer.voucherRedemption !is Exhausted) {
                    OfferRedemptionView(
                        offer.voucherRedemption,
                        Modifier.padding(top = 30.dp, bottom = 20.dp),
                        offer.discount,
                        onRequestClick = onRequestClick,
                        // onRedeemClick = onRedeemClick,
                        website = offer.websiteUrl
                    )
                    Text(
                        text = offer.storeType.text(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }


                de.cleema.android.core.components.MarkdownText(
                    markdown = offer.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                offer.location?.let {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Divider()

                        MapView(
                            it.coordinates,
                            modifier = Modifier
                                .height(180.dp)
                        ) {
                            onLocationClick(it)
                        }
                    }
                }
            }
        }
    }
}
