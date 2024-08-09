package de.cleema.android.magazine.details

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.cleema.android.core.common.formatted
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTextButton
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.FormatStyle

@Composable
fun MagazineItemDetailCard(
    magazineItem: de.cleema.android.core.models.MagazineItem,
    modifier: Modifier = Modifier,
    onFavClick: () -> Unit,
    didScrollToBottom: () -> Unit
) {
    val scrollState: ScrollState = rememberScrollState()

    LaunchedEffect(key1 = scrollState.value) {
        if (scrollState.value == scrollState.maxValue) {
            didScrollToBottom()
        }
    }

    Card(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            de.cleema.android.core.components.RemoteImageView(
                remoteImage = magazineItem.image,
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
                        if (magazineItem.type == de.cleema.android.core.models.MagazineItem.ItemType.NEWS) {
                            Text(
                                magazineItem.date.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .formatted(FormatStyle.MEDIUM)
                            )
                        }
                        Text(
                            text = magazineItem.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    de.cleema.android.core.components.FavoriteButton(magazineItem.faved, onCheckedChange = onFavClick)
                }

                de.cleema.android.core.components.MarkdownText(
                    magazineItem.description,
                    style = MaterialTheme.typography.titleSmall
                )

                if (magazineItem.tags.isNotEmpty()) {
                    Divider(thickness = 1.dp, color = Color.LightGray)

                    Row {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            magazineItem.tags.forEach { tag ->
                                CleemaTextButton(onClick = { /* TODO */ }) {
                                    Text(
                                        "#${tag.value}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Action
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
