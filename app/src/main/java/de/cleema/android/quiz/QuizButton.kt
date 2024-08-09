/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.components.MarkdownText
import de.cleema.android.core.models.Quiz.Choice.A
import de.cleema.android.core.styling.Action
import de.cleema.android.quiz.AnswerQuizUiState.*
import java.util.*


@Composable
fun QuizButton(
    modifier: Modifier = Modifier,
    viewModel: AnswerQuizViewModel = hiltViewModel(),
    onSolveClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuizButton(
        modifier = modifier,
        state = uiState,
        onSolveClick = onSolveClick
    )
}

@Composable
fun QuizButton(
    modifier: Modifier = Modifier,
    state: AnswerQuizUiState,
    onSolveClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.shadow(19.dp, RoundedCornerShape(10.dp)),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd, modifier = Modifier
                    .matchParentSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wave_long),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.quiz_headline_title),
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (state is Loading) {
                        Spacer(modifier = Modifier.weight(1f))
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.width(16.dp)
                        )
                    }
                }

                when (state) {
                    is Pending -> {
                        MarkdownText(
                            state.question,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(horizontalArrangement = Arrangement.End) {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                contentPadding = PaddingValues(horizontal = 32.dp),
                                onClick = onSolveClick,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = White,
                                    containerColor = Action
                                ),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(
                                    stringResource(R.string.quiz_solve_button_title),
                                    color = White
                                )
                            }
                        }
                    }
                    is Answered -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = stringResource(R.string.quiz_already_answered),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(R.string.quiz_check_later),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Preview("Pending", widthDp = 320, heightDp = 100)
@Composable
fun QuizButtonPendingPreview() {
    QuizButton(
        modifier = Modifier.fillMaxSize(),
        state = Pending(question = "Lorem ipsum", choices = sortedMapOf(), quizId = UUID.randomUUID())
    )
}

@Preview("Loading", widthDp = 320, heightDp = 60)
@Composable
fun QuizButtonLoadingPreview() {
    QuizButton(modifier = Modifier.fillMaxSize(), state = Loading)
}

@Preview("Answered", widthDp = 320, heightDp = 100)
@Composable
fun QuizButtonAnsweredPreview() {
    QuizButton(
        modifier = Modifier.fillMaxSize(),
        state = Answered(question = "", choice = A, answer = "", explanation = "", isCorrect = false)
    )
}
