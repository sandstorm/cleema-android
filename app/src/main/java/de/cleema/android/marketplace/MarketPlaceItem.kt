/*
 * Created by Kumpels and Friends on 2022-12-06
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.cleema.android.R
import de.cleema.android.core.models.MarketItem.StoreType.Online
import de.cleema.android.core.models.MarketItem.StoreType.Shop
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun MarketPlaceItem(
    title: String,
    description: String,
    discount: Int,
    storeType: de.cleema.android.core.models.MarketItem.StoreType,
    modifier: Modifier = Modifier,
    image: de.cleema.android.core.models.RemoteImage?,
) {
    Card(modifier = modifier.fillMaxSize(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(
            modifier = Modifier
                .padding(PaddingValues(all = 8.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.height(40.dp),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp
            )
            de.cleema.android.core.components.RemoteImageView(
                remoteImage = image,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )

            if (discount > 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row {
                        Text(
                            text = stringResource(R.string.marketplace_voucher_percentage_format_string, discount),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alignBy(LastBaseline)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.market_item_discount),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alignBy(LastBaseline)
                        )
                    }

                    Text(
                        text = storeType.text(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(text = description, style = MaterialTheme.typography.titleSmall)
            }

        }
    }
}

@Composable
fun de.cleema.android.core.models.MarketItem.StoreType.text(): String =
    when (this) {
        Online -> stringResource(R.string.market_item_online)
        Shop -> stringResource(R.string.market_item_shop)
    }

@Preview("Marketplace item", widthDp = 200)
@Composable
private fun MarketPlaceItemPreview() {
    CleemaTheme {
        MarketPlaceItem(
            title = "Title",
            description = "Description",
            discount = 22,
            image = de.cleema.android.core.models.RemoteImage.of(
                url = "https://loremflickr.com/1035/62?random=1",
                size = de.cleema.android.core.models.Size(345f, 208f)
            ),
            storeType = Shop
        )
    }
}
