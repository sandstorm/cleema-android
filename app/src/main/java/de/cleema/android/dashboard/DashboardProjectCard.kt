/*
 * Created by Kumpels and Friends on 2022-12-09
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.models.Region.Companion.LEIPZIG
import de.cleema.android.core.styling.CleemaTheme
import kotlinx.datetime.Clock
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardProjectCard(
    project: de.cleema.android.core.models.Project,
    modifier: Modifier = Modifier,
    waveMirrored: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(19.dp, RoundedCornerShape(10.dp)),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Box(
            contentAlignment = Alignment.TopEnd, modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.wavelarge),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scaleX = if (waveMirrored) 1f else -1f, scaleY = 1f)
                )

                de.cleema.android.core.components.RemoteImageView(
                    remoteImage = project.teaserImage,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .padding(bottom = 18.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    project.title,
                    modifier = Modifier.fillMaxWidth(0.92f),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    project.partner.name,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (project.isFaved) {
                de.cleema.android.core.components.BadgeView(text = "★")
            }
        }
    }
}

@Preview(widthDp = 300)
@Composable
fun DashboardProjectCardPreview() {
    CleemaTheme {
        DashboardProjectCard(
            project = de.cleema.android.core.models.Project(
                id = UUID.randomUUID(),
                title = "Essbares öffentliches Stadtgrün",
                description = "Demo Project Description",
                date = Clock.System.now(),
                partner = de.cleema.android.core.models.Partner(
                    UUID.randomUUID(),
                    "Demo Partner",
                    url = "https://api.cleema.app/uploads/demo_Project_3x_deb31f738a.png"
                ),
                region = LEIPZIG,
                teaserImage = de.cleema.android.core.models.RemoteImage.of(
                    "https://api.cleema.app/uploads/demo_Project_Icon_3x_5a14197e97.png",
                    size = de.cleema.android.core.models.Size(453f / 3f, 429f / 3f)
                ),
                image = null,
                summary = "",
                location = de.cleema.android.core.models.Location.LEIPZIG,
                isFaved = true,
                goal = de.cleema.android.core.models.Goal.Involvement(10, 100, true),
            ),
            waveMirrored = true
        )
    }
}
