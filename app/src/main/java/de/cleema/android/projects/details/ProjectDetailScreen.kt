/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.components.map.MapView
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.ContentBackground
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@Composable
fun ProjectDetailScreen(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        ContentBackground(modifier = Modifier.fillMaxSize())
        ProjectDetailContent()
    }
}


@Composable
fun ProjectDetailContent(
    modifier: Modifier = Modifier,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    when (val state = viewModel.uiState.collectAsStateWithLifecycle().value) {
        is ProjectUiState.Success -> ProjectDetailScrollingContent(
            state.project,
            modifier,
            onJoinClick = viewModel::joinClicked,
            onFavClicked = viewModel::favClicked,
            onPartnerClicked = viewModel::onPartnerClicked,
            onLocationClick = viewModel::onLocationClicked
        )
        is ProjectUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
        is ProjectUiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ProjectDetailScrollingContent(
    project: de.cleema.android.core.models.Project,
    modifier: Modifier = Modifier,
    onJoinClick: () -> Unit = {},
    onFavClicked: () -> Unit = {},
    onPartnerClicked: () -> Unit,
    onLocationClick: () -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            de.cleema.android.core.components.RemoteImageView(
                remoteImage = project.image, modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp),
                contentScale = ContentScale.Crop
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    project.title,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                de.cleema.android.core.components.FavoriteButton(project.isFaved, onCheckedChange = onFavClicked)
            }

            ProjectPhase(project)

            val goal = project.goal
            if (goal is de.cleema.android.core.models.Goal.Involvement && project.phase is de.cleema.android.core.models.Phase.Pre) {
                InvolvementGoalView(goal) {
                    if (goal.joined) {
                        openDialog.value = true
                    } else {
                        onJoinClick()
                    }
                }
            }

            Divider()

            de.cleema.android.core.components.MarkdownText(project.description)

            PartnerContent(project.partner, onPartnerClicked = onPartnerClicked)

            Divider()

            project.location?.let {
                MapView(
                    coordinates = it.coordinates,
                    modifier = Modifier
                        .height(180.dp),
                    onClick = onLocationClick
                )
            }
        }
        if (openDialog.value) {
            LeaveProjectAlert(project.title, onJoinClick = {
                onJoinClick()
                openDialog.value = false
            }, onDismissClick = {
                openDialog.value = false
            })
        }
    }
}

private fun LocalDateTime.formattedDateTime(style: FormatStyle): String =
    DateTimeFormatter.ofLocalizedDateTime(style).format(this.toJavaLocalDateTime())

@Composable
fun de.cleema.android.core.models.Project.formattedProjectText(): String =
    when (phase) {
        de.cleema.android.core.models.Phase.Pre -> stringResource(
            id = R.string.project_detail_phase_pre,
            this.date.toLocalDateTime(currentSystemDefault())
                .formattedDateTime(FormatStyle.SHORT)
        )
        de.cleema.android.core.models.Phase.Cancelled -> stringResource(id = R.string.project_detail_phase_cancelled)
        de.cleema.android.core.models.Phase.Post -> stringResource(id = R.string.project_detail_phase_finished)
        de.cleema.android.core.models.Phase.Within -> stringResource(id = R.string.project_detail_phase_active)
    }

@Preview("Project Detail Screen")
@Composable
fun ProjectDetailScreenPreview() {
    CleemaTheme {
        Box {
            ContentBackground(modifier = Modifier.fillMaxSize())
            ProjectDetailScrollingContent(
                de.cleema.android.core.models.Project(
                    id = UUID.randomUUID(),
                    title = "My cleaning Project",
                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n" +
                            "\n" +
                            "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.",
                    date = Clock.System.now(),
                    partner = de.cleema.android.core.models.Partner(
                        name = "Bike 24",
                        url = "https://bike24.de",
                        description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
                    ),
                    region = de.cleema.android.core.models.Region.LEIPZIG,
                    teaserImage = null,
                    image = null,
                    summary = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy",
                    location = de.cleema.android.core.models.Location.LEIPZIG,
                    isFaved = true,
                    goal = de.cleema.android.core.models.Goal.Involvement(10, 100, true),
                ),
                onPartnerClicked = {},
                onLocationClick = {}
            )
        }
    }
}
