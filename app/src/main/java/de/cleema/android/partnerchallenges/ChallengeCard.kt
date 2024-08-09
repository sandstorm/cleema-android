/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.partnerchallenges

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.common.formatted
import de.cleema.android.core.models.Size
import de.cleema.android.core.styling.News
import kotlinx.datetime.*
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeCard(
    title: String,
    teaserText: String,
    image: de.cleema.android.core.models.RemoteImage?,
    startDate: Instant,
    endDate: Instant,
    partner: de.cleema.android.core.models.Partner? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                de.cleema.android.core.components.RemoteImageView(
                    image,
                    Modifier
                        .fillMaxWidth()
                        .height(225.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = teaserText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    partner?.let { partner ->
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(verticalAlignment = Bottom) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.partner_heading), fontWeight = FontWeight.SemiBold)
                                Text(partner.name)
                            }

                            de.cleema.android.core.components.RemoteImageView(
                                remoteImage = partner.logo,
                                modifier = Modifier.size(100.dp, 36.dp)
                            )
                        }
                    }
                }
            }

            de.cleema.android.core.components.BadgeView(
                text = if (endDate > Clock.System.now()) startDate.toLocalDateTime(TimeZone.UTC).formatted(FormatStyle.MEDIUM)
                            else stringResource(R.string.challenge_date_finished), News
            )
        }
    }
}

@Preview("Challenge", widthDp = 320)
@Composable
fun ChallengeCardPreview() {
    ChallengeCard(
        title = "Challenge",
        teaserText = "Lorem ipsum dolor sit amet, consectetur adipisici elit",
        image = de.cleema.android.core.models.RemoteImage.of(
            "https://cleema.app/uploads/Zubereiteter_Fisch_12ecef0ba1.jpg",
            Size(2048f, 1365f)
        ),
        startDate = Clock.System.now(),
        endDate = Clock.System.now(),
        partner = de.cleema.android.core.models.Partner.DEMO,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    )
}
