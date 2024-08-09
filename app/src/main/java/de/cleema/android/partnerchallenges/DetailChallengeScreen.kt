/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.partnerchallenges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.components.MarkdownText
import de.cleema.android.core.models.Challenge
import de.cleema.android.core.models.Challenge.Kind
import de.cleema.android.core.models.Challenge.Kind.Partner
import de.cleema.android.core.models.collectiveProgress
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.ContentBackground
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun DetailChallengeScreen(
    modifier: Modifier = Modifier,
    viewModel: PartnerChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailChallengeScreen(
        modifier = modifier,
        state = uiState,
        participateClicked = viewModel::socialButtonTapped,
        onConfirmClick = viewModel::confirmLeave,
        onDismissAlertClick = viewModel::dismissAlert,
        onOpenPartnerClick = viewModel::partnerTapped
    )
}

@Composable
fun DetailChallengeScreen(
    modifier: Modifier = Modifier,
    state: PartnerChallengeUiState,
    participateClicked: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissAlertClick: () -> Unit,
    onOpenPartnerClick: () -> Unit
) {
    Box(modifier = modifier) {
        ContentBackground()
        when (state) {
            PartnerChallengeUiState.Loading -> CircularProgressIndicator()
            is PartnerChallengeUiState.NotFound -> de.cleema.android.core.components.ErrorScreen(message = state.message)
            is PartnerChallengeUiState.Success -> ChallengeDetails(
                state = state,
                participateClicked = participateClicked,
                onConfirmClick = onConfirmClick,
                onDismissAlertClick = onDismissAlertClick,
                onOpenPartnerClick = onOpenPartnerClick
            )
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ChallengeDetails(
    modifier: Modifier = Modifier,
    state: PartnerChallengeUiState.Success,
    participateClicked: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissAlertClick: () -> Unit,
    onOpenPartnerClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.challenge.image?.let {
                de.cleema.android.core.components.RemoteImageView(
                    remoteImage = it.image, modifier = Modifier
                        .fillMaxWidth()
                        .height(225.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = state.challenge.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            ChallengeStart(state.challenge.startDate, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.persons), contentDescription = null)
                Text(
                    text = stringResource(
                        R.string.partner_challenge_participant_count,
                        state.challenge.numberOfUsersJoined
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DefaultText
                )

                Spacer(modifier = Modifier.weight(1f))

                if (state.challenge.endDate > Clock.System.now()) {
                    Button(
                        onClick = participateClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = Action),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(id = if (state.challenge.joined) R.string.involvement_action_leave_label else R.string.involvement_action_join_label),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        stringResource(R.string.challenge_date_finished),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DefaultText,
                        textAlign = TextAlign.Right,
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            if (state.challenge.kind is Challenge.Kind.Collective && state.challenge.collectiveGoalAmount != null) {
                Text(stringResource(R.string.challenge_collective_progress_header))

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                            )
                        ) {
                            append(state.challenge.collectiveProgress.toString())
                        }
                        append(
                            " " + stringResource(R.string.challenge_points) + " " + stringResource(
                                R.string.answer_challenge_of
                            )
                        )

                        withStyle(
                            style = SpanStyle(
                                fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                            )
                        ) {
                            append(" " + state.challenge.collectiveGoalAmount.toString())
                        }
                        append(" " + stringResource(R.string.answer_challenge_progress_last))
                    }
                )

                state.challenge.collectiveProgress?.let {
                    LinearProgressIndicator(
                        progress = if (state.challenge.collectiveProgress == null) 0.toFloat() else state.challenge.collectiveProgress!!.toFloat() / state.challenge.collectiveGoalAmount!!.toFloat(),
                        color = Dimmed,
                        trackColor = Accent.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .fillMaxWidth()
                    )
                }

                Divider()
            }

            MarkdownText(
                state.challenge.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            val kind = state.challenge.kind
            if (kind is Kind.Partner) {
                PartnerInfo(partner = kind.partner, onOpenPartnerClick = onOpenPartnerClick)
            } else if (kind is Kind.Collective) {
                PartnerInfo(partner = kind.partner, onOpenPartnerClick = onOpenPartnerClick)
            }
        }

        if (state.showsAlert) {
            LeaveChallengeAlert(
                state.challenge.title,
                onConfirmClick = onConfirmClick,
                onDismissClick = onDismissAlertClick
            )
        }
    }

}

@Preview("Challenge", widthDp = 320)
@Composable
fun DetailChallengeScreenPreview() {
    CleemaTheme {
        DetailChallengeScreen(
            modifier = Modifier.fillMaxWidth(),
            state = PartnerChallengeUiState.Success(
                de.cleema.android.core.models.Challenge.of(
                    title = "Challenge",
                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, con",
                    kind = Partner(de.cleema.android.core.models.Partner(name = "Partner")),
                    endDate = Clock.System.now().minus((3).days)
                )
            ),
            participateClicked = {},
            onConfirmClick = {},
            onDismissAlertClick = {},
            onOpenPartnerClick = {}
        )
    }
}
