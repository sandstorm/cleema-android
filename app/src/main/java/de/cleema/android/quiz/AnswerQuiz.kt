/*
 * Created by Kumpels and Friends on 2022-12-19
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.components.MarkdownText
import de.cleema.android.core.models.Quiz.Choice
import de.cleema.android.core.models.Quiz.Choice.*
import de.cleema.android.core.styling.*
import java.util.*


@Composable
fun AnswerQuiz(
    modifier: Modifier = Modifier,
    viewModel: AnswerQuizViewModel = hiltViewModel()
) {
    val uiState: AnswerQuizUiState by viewModel.uiState.collectAsStateWithLifecycle()
    ContentBackground()
    AnswerQuiz(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        state = uiState,
        onAnswerClicked = { viewModel.answer(it) }
    )
}

@Composable
fun AnswerQuiz(
    modifier: Modifier = Modifier,
    state: AnswerQuizUiState,
    onAnswerClicked: (Choice) -> Unit = {}
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Card(modifier = Modifier) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.quiz),
                    contentDescription = "Quiz heading image"
                )
                state.question?.let {
                    MarkdownText(
                        it,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 10
                    )
                }

                when (state) {
                    is AnswerQuizUiState.Answered -> {
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            QuizChoice(
                                onAnswerClicked = {},
                                choice = state.choice,
                                text = state.answer,
                                isCorrectAnswer = state.isCorrect
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = state.resultTitle(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                MarkdownText(
                                    state.explanation,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }
                    }
                    AnswerQuizUiState.Loading -> CircularProgressIndicator()
                    is AnswerQuizUiState.Pending -> Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for ((choice, text) in state.choices.toList()) {
                            QuizChoice(onAnswerClicked, choice, text, false)
                        }
                    }
                    is AnswerQuizUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun QuizChoice(
    onAnswerClicked: (Choice) -> Unit,
    choice: Choice,
    text: String,
    isCorrectAnswer: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = MaterialTheme.shapes.medium,
        onClick = { onAnswerClicked(choice) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .background(color = if (isCorrectAnswer) Accent else Answer)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = choice.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isCorrectAnswer) Dimmed else Action)
                    .padding(horizontal = 14.dp)
            ) {
                MarkdownText(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    linkColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun AnswerQuizUiState.Answered.resultTitle(): String =
    stringResource(if (isCorrect) R.string.quiz_result_correct_answer else R.string.quiz_result_wrong_answer)


@Preview(name = "Answer Quiz", widthDp = 320)
@Composable
fun AnswerQuizPreview() {
    AnswerQuiz(
        modifier = Modifier.fillMaxWidth(),
        state = AnswerQuizUiState.Pending(
            question = "Lorem ipsum dolor sit amet",
            sortedMapOf(
                A to "Lorem ipsum",
                B to "Dolor sit amet",
                C to "Consetetur sadipscing elitr",
                D to "Sed diam nonumy"
            ),
            quizId = UUID.randomUUID()
        )
    )
}

@Preview(name = "Correctly answered Quiz", widthDp = 320)
@Composable
fun AnsweredQuizPreview() {
    AnswerQuiz(
        modifier = Modifier.fillMaxWidth(),
        state = AnswerQuizUiState.Answered(
            question = "Lorem ipsum dolor sit amet",
            choice = A,
            answer = "42",
            explanation = "The meaning of life, the universe, and everything",
            isCorrect = true
        )
    )
}


@Preview(name = "False answered Quiz", widthDp = 320)
@Composable
fun FalseAnsweredQuizPreview() {
    AnswerQuiz(
        modifier = Modifier.fillMaxWidth(),
        state = AnswerQuizUiState.Answered(
            question = "Lorem ipsum dolor sit amet",
            choice = A,
            answer = "42",
            explanation = "The meaning of life, the universe, and everything",
            isCorrect = false
        )
    )
}

