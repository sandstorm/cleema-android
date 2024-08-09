/*
 * Created by Kumpels and Friends on 2022-12-05
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.ContentBackground
import java.util.*

@Composable
fun MarketPlaceItemList(
    offers: List<de.cleema.android.core.models.MarketItem>,
    modifier: Modifier = Modifier,
    onOfferClick: (UUID) -> Unit = {}
) {
    de.cleema.android.core.components.SameHeightLazyGrid(
        columnsCount = 2,
        content = offers,
        modifier = modifier,
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) { item, itemModifier ->
        MarketPlaceItem(
            title = item.title,
            description = item.subTitle,
            discount = item.discount,
            storeType = item.storeType,
            image = item.image,
            modifier = itemModifier
                .clickable { onOfferClick(item.id) }
        )
    }
}

@Preview("Marketplace list")
@Composable
private fun MarketPlaceItemListPreview() {
    CleemaTheme {
        ContentBackground()
        MarketPlaceItemList(offers = de.cleema.android.core.models.MarketItem.demo)
    }
}
