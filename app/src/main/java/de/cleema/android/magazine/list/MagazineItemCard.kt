/*
 * Created by Kumpels and Friends on 2022-11-18
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.common.formatted
import de.cleema.android.core.models.*
import de.cleema.android.core.models.MagazineItem.ItemType
import de.cleema.android.core.models.MagazineItem.ItemType.*
import de.cleema.android.core.styling.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagazineItemCard(
    title: String,
    teaser: String,
    date: Instant,
    tags: List<de.cleema.android.core.models.Tag>,
    image: de.cleema.android.core.models.RemoteImage?,
    type: ItemType,
    faved: Boolean,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
    onFavClick: () -> Unit,
) {
    Card(
        onClick = onMoreClick,
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = White,
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                de.cleema.android.core.components.RemoteImageView(
                    remoteImage = image,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(208.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier.padding(
                        PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 20.dp
                        )
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (type == NEWS) {
                                Text(
                                    date.toLocalDateTime(TimeZone.currentSystemDefault())
                                        .formatted(FormatStyle.MEDIUM)
                                )
                            }
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        de.cleema.android.core.components.FavoriteButton(
                            faved, onCheckedChange = onFavClick
                        )
                    }

                    de.cleema.android.core.components.MarkdownText(
                        teaser,
                        style = MaterialTheme.typography.titleMedium,
                        onClick = onMoreClick
                    )

                    Divider(thickness = 1.dp, color = Color.LightGray)

                    Row {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            tags.forEach { tag ->
                                CleemaTextButton(onClick = { /* TODO */ }) {
                                    Text(
                                        "#${tag.value}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Action
                                    )
                                }
                            }
                        }

                        CleemaTextButton(onClick = onMoreClick) {
                            Text(stringResource(id = R.string.readMore), color = Action)
                        }
                    }
                }
            }

            de.cleema.android.core.components.BadgeView(text = type.badgeText(), color = type.badgeColor)
        }
    }
}

val ItemType.badgeColor
    get() = when (this) {
        NEWS -> News
        TIP -> Tip
    }

@Composable
fun ItemType.badgeText(): String {
    return when (this) {
        NEWS -> stringResource(id = R.string.magazine_badge_news)
        TIP -> stringResource(id = R.string.magazine_badge_tip)
    }
}

@Preview("News item", widthDp = 600)
@Composable
private fun MagazineItemPreview() {
    CleemaTheme {
        val magazineItem = de.cleema.android.core.models.MagazineItem.demo[0]
        MagazineItemCard(
            title = magazineItem.title,
            teaser = magazineItem.description,
            date = magazineItem.date,
            tags = magazineItem.tags,
            image = magazineItem.image,
            type = magazineItem.type,
            faved = magazineItem.faved,
            onMoreClick = {},
            onFavClick = {}
        )
    }
}
