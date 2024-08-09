/*
 * Created by Kumpels and Friends on 2022-11-18
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.list

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import de.cleema.android.R
import de.cleema.android.core.models.Size
import de.cleema.android.core.styling.CleemaTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun ProjectCard(
    project: de.cleema.android.core.models.Project,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFavClick: () -> Unit
) {
    ProjectCard(
        title = project.title,
        summary = project.summary,
        image = project.teaserImage,
        date = project.date,
        partner = project.partner.name,
        modifier = modifier,
        faved = project.isFaved,
        onClick = onClick,
        onFavClick = onFavClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCard(
    title: String,
    summary: String,
    image: de.cleema.android.core.models.RemoteImage?,
    date: Instant,
    partner: String,
    faved: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onFavClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = White,
        ),
//        elevation = CardDefaults.cardElevation(10.dp),
//        elevation = CardElevation,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                de.cleema.android.core.components.FavoriteButton(faved, onCheckedChange = onFavClick)
            }

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val (textRef, startRef, spacerRef, imageRef) = createRefs()

                de.cleema.android.core.components.RemoteImageView(
                    remoteImage = image,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .constrainAs(imageRef) {
                            start.linkTo(textRef.end)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        }
                )

                Text(
                    summary, modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .constrainAs(textRef) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )

                Spacer(modifier = Modifier.constrainAs(spacerRef) {
                    top.linkTo(textRef.bottom)
                    bottom.linkTo(startRef.top)
                })

                de.cleema.android.core.components.DateLabel(
                    headingRes = R.string.start_date_header,
                    date = date,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .constrainAs(startRef) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                )
            }

            Divider()

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Partner", fontWeight = FontWeight.SemiBold)
                Text(partner)
            }
        }
    }
}

@Preview("News item", widthDp = 320)
@Composable
fun ProjectCardPreview() {
    CleemaTheme {
        ProjectCard(
            title = "Kröten schlucken",
            summary = "Roh oder gekocht",
            image = de.cleema.android.core.models.RemoteImage.of(
                "https://i.picsum.photos/id/641/200/300.jpg?hmac=TpiMIigzR3rlnmP84Df902LYzuV4pApn7Tq6lCYic0A",
                Size(320f, 100f)
            ),
            date = Clock.System.now(),
            partner = "Froschteich e.V.",
            faved = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
