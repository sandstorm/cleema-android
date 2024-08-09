/*
 * Created by Kumpels and Friends on 2022-11-17
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.ContentBackground
import java.util.*

@Composable
fun MagazineItemsList(
    magazineItems: List<de.cleema.android.core.models.MagazineItem>,
    modifier: Modifier = Modifier,
    onMoreClick: (UUID) -> Unit,
    onFavClick: (UUID) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        items(magazineItems) { item ->
            MagazineItemCard(
                title = item.title,
                teaser = item.teaser,
                date = item.date,
                tags = item.tags,
                image = item.image,
                type = item.type,
                faved = item.faved,
                onMoreClick = {
                    onMoreClick(item.id)
                },
                onFavClick = {
                    onFavClick(item.id)
                }
            )
        }
    }
}

@Preview("News list")
@Composable
private fun NewsItemListPreview() {
    CleemaTheme {
        Box {
            ContentBackground(modifier = Modifier.matchParentSize())
            MagazineItemsList(
                de.cleema.android.core.models.MagazineItem.demo,
                onMoreClick = {},
                onFavClick = {}
            )
        }
    }
}
