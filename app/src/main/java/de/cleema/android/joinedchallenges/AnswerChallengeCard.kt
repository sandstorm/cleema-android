/*
 * Created by Kumpels and Friends on 2023-02-02
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.joinedchallenges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.common.formatted
import de.cleema.android.core.components.RemoteImageView
import de.cleema.android.core.models.Challenge
import de.cleema.android.core.models.JoinedChallenge.Answer.FAILED
import de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED
import de.cleema.android.core.models.collectiveProgress
import de.cleema.android.core.models.duration
import de.cleema.android.core.models.numberOfUnitsDone
import de.cleema.android.core.models.progress
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.Dimmed
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.Value
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.Value.Status.*
import de.cleema.android.partnerchallenges.LeaveChallengeAlert
import de.cleema.android.partnerchallenges.PartnerInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import java.time.format.FormatStyle
import kotlin.random.Random.Default.nextInt

@Composable
fun AnswerChallengeScreen(
    modifier: Modifier = Modifier,
    viewModel: JoinedChallengeViewModel = hiltViewModel(),
    onNotFound: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AnswerChallengeScreen(
        modifier = modifier, state = uiState,
        onNotFound = onNotFound,
        onFailClicked = { viewModel.answer(FAILED) },
        onSucceedClicked = { viewModel.answer(SUCCEEDED) },
        onShowConfirmationClicked = viewModel::leaveTapped,
        onOpenPartnerClick = viewModel::partnerTapped,
        onLeaveDialogClicked = { shouldLeave ->
            if (shouldLeave) {
                viewModel.confirmLeave()
            } else {
                viewModel.cancelLeaveDialog()
            }
        }
    )
}

@Composable
fun AnswerChallengeScreen(
    modifier: Modifier = Modifier,
    state: JoinedChallengeUiState,
    onNotFound: () -> Unit,
    onFailClicked: () -> Unit,
    onSucceedClicked: () -> Unit,
    onShowConfirmationClicked: () -> Unit,
    onLeaveDialogClicked: (shouldLeave: Boolean) -> Unit,
    onOpenPartnerClick: () -> Unit
) {
    when (state) {
        JoinedChallengeUiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        JoinedChallengeUiState.NotJoined -> {
            LaunchedEffect(state) {
                onNotFound()
            }
        }

        is JoinedChallengeUiState.Error -> de.cleema.android.core.components.ErrorScreen(
            message = state.reason,
            modifier
        )

        is Value -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
            ) {
                AnswerChallengeCard(
                    state = state,
                    failClicked = onFailClicked,
                    succeedClicked = onSucceedClicked,
                    leaveClicked = onShowConfirmationClicked,
                    onLeaveDialogClicked = onLeaveDialogClicked,
                    onOpenPartnerClick = onOpenPartnerClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class)
@Composable
fun AnswerChallengeCard(
    state: Value,
    modifier: Modifier = Modifier,
    failClicked: () -> Unit,
    succeedClicked: () -> Unit,
    leaveClicked: () -> Unit,
    onLeaveDialogClicked: (shouldLeave: Boolean) -> Unit,
    onOpenPartnerClick: () -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Surface(
            modifier = Modifier.padding(horizontal = 20.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.joinedChallenge.challenge.image?.let {
                    RemoteImageView(
                        state.joinedChallenge.challenge.image?.image,
                        Modifier
                            .fillMaxWidth()
                            .height(225.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = stringResource(R.string.answer_challenge_heading),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = state.joinedChallenge.challenge.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    de.cleema.android.core.components.DateLabel(
                        R.string.start_date_header,
                        state.joinedChallenge.challenge.startDate
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    de.cleema.android.core.components.DateLabel(
                        R.string.end_date_header,
                        state.joinedChallenge.challenge.endDate
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    if (state.joinedChallenge.challenge.endDate <= Clock.System.now()) {
                        Text(stringResource(R.string.challenge_date_finished), fontWeight = FontWeight.SemiBold)
                    }
                }

                if (state.joinedChallenge.challenge.kind is Challenge.Kind.Collective) {
                    Text(stringResource(R.string.challenge_collective_progress_header))

                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                                    fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                                )
                            ) {
                                append(state.joinedChallenge.challenge.collectiveProgress.toString())
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
                                append(" " + state.joinedChallenge.challenge.collectiveGoalAmount.toString())
                            }
                            append(" " + stringResource(R.string.answer_challenge_progress_last))
                        }
                    )

                    LinearProgressIndicator(
                        progress = state.joinedChallenge.collectiveProgress.toFloat(),
                        color = Dimmed,
                        trackColor = Accent.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .fillMaxWidth()
                    )

                    Divider()

                    Text(stringResource(R.string.challenge_personal_progress_header))
                }

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                            )
                        ) {
                            append(state.joinedChallenge.numberOfUnitsDone.toString())
                        }
                        append(
                            " " + state.joinedChallenge.challenge.duration.title() + " " + stringResource(
                                R.string.answer_challenge_of
                            )
                        )

                        withStyle(
                            style = SpanStyle(
                                fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                            )
                        ) {
                            append(" " + state.joinedChallenge.challenge.duration.valueCount.toString())
                        }
                        append(" " + stringResource(R.string.answer_challenge_progress_last))
                    }
                )

                LinearProgressIndicator(
                    progress = state.joinedChallenge.progress.toFloat(),
                    color = Dimmed,
                    trackColor = Accent.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxWidth()
                )

                if (state.joinedChallenge.challenge.endDate > Clock.System.now()) {
                    state.status?.let {
                        Text(text = it.title(), style = MaterialTheme.typography.bodySmall)
                    }

                    if (state.status?.isPending == true) {
                        Row(modifier = Modifier.align(CenterHorizontally)) {
                            Button(
                                onClick = failClicked,
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.fail_button),
                                    contentDescription = ""
                                )
                            }

                            Spacer(Modifier.width(40.dp))

                            Button(
                                onClick = succeedClicked,
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp)
//                    modifier = Modifier.size(80.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.success_button),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }

                if (state.showsLeaveAlert) {
                    LeaveChallengeAlert(
                        title = state.joinedChallenge.challenge.title,
                        onConfirmClick = { onLeaveDialogClicked(true) },
                        onDismissClick = { onLeaveDialogClicked(false) }
                    )
                }

                Divider()

                Text(state.joinedChallenge.challenge.description, style = MaterialTheme.typography.bodySmall)

                if (state.progresses.isNotEmpty()) {
                    Divider()

                    UserProgressList(
                        users = state.progresses,
                        maxValue = state.joinedChallenge.challenge.duration.valueCount,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                val kind = state.joinedChallenge.challenge.kind
                if (kind is Challenge.Kind.Partner) {
                    PartnerInfo(partner = kind.partner, onOpenPartnerClick = onOpenPartnerClick)
                } else if (kind is Challenge.Kind.Collective) {
                    PartnerInfo(partner = kind.partner, onOpenPartnerClick = onOpenPartnerClick)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.joinedChallenge.challenge.endDate > Clock.System.now()) {
                        TextButton(
                            onClick = leaveClicked,
                            enabled = !state.showsLeaveAlert,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Dimmed
                            ),
                        ) {
                            Text(
                                text = stringResource(R.string.answer_challenge_leave),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.SemiBold,

                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Value.Status.title(): String = when (this) {
    Answered -> stringResource(R.string.challenge_answered)
    Expired -> stringResource(R.string.challenge_expired)
    is Upcoming -> stringResource(R.string.challenge_upcoming_days, days)
    is Pending -> date.formatted(FormatStyle.MEDIUM)
    is PendingWeekly -> stringResource(if (pendingIndex == currentWeekIndex) R.string.challenge_pending_current_week else R.string.challenge_pending_last_week)
}

@Preview("Answer challenge", widthDp = 320)
@Composable
fun AnswerChallengeScreenPreview() {
    CleemaTheme {
        AnswerChallengeScreen(
            state = Value(
                de.cleema.android.core.models.JoinedChallenge(
                    challenge = de.cleema.android.core.models.Challenge.of(
                        title = "Fordere dich",
                        description = LoremIpsum(20).values.joinToString(" ")
                    ),
                    answers = (1..nextInt(
                        from = 1,
                        until = 31
                    )).associateWith { SUCCEEDED }
                ),
                status = Pending(1, LocalDate(2022, 11, 30)),
                progresses = listOf(
                    de.cleema.android.core.models.UserProgress(
                        25,
                        8,
                        de.cleema.android.core.models.SocialUser(username = "Clara Cleema")
                    ),
                    de.cleema.android.core.models.UserProgress(
                        25,
                        10,
                        de.cleema.android.core.models.SocialUser(username = "Bernd Brot")
                    ),
                    de.cleema.android.core.models.UserProgress(
                        25,
                        17,
                        de.cleema.android.core.models.SocialUser(username = "E.T.")
                    )
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Dimmed),
            onLeaveDialogClicked = {},
            onNotFound = {},
            onFailClicked = {},
            onSucceedClicked = {},
            onShowConfirmationClicked = {},
            onOpenPartnerClick = {}
        )
    }
}
