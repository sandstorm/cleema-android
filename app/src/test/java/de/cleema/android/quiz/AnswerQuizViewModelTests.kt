package de.cleema.android.quiz

import de.cleema.android.core.models.QuizState
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.quiz.AnswerQuizUiState.Loading
import de.cleema.android.quiz.AnswerQuizUiState.Pending
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock.System
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnswerQuizViewModelTests {
    private lateinit var sut: AnswerQuizViewModel
    private lateinit var quizRepository: FakeQuizRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        quizRepository = FakeQuizRepository()
        sut = AnswerQuizViewModel(quizRepository)
    }

    @Test
    fun `Answering a quiz with incorrect answer`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val quizState = de.cleema.android.core.models.QuizState(FAKE_QUIZ1, streak = 42, answer = null)
        quizRepository.quizChannel.send(Result.success(quizState))

        assertEquals(
            Pending(
                question = quizState.quiz.question,
                choices = quizState.quiz.choices.toSortedMap(),
                quizId = quizState.quiz.id
            ), sut.uiState.value
        )

        val choice = quizState.quiz.choices.keys.filter { it != quizState.quiz.correctAnswer }.random()
        sut.answer(choice)

        assertEquals(choice, quizRepository.answeredChoice)
        assertEquals(quizState.quiz.id, quizRepository.answeredId)

        quizRepository.quizChannel.send(
            Result.success(
                quizState.copy(
                    answer = de.cleema.android.core.models.QuizState.Answer(System.now(), choice)
                )
            )
        )

        assertEquals(
            AnswerQuizUiState.Answered(
                question = quizState.quiz.question,
                choice = choice,
                answer = quizState.quiz.choices[choice]!!,
                explanation = quizState.quiz.explanation,
                isCorrect = false
            ),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Answering a quiz with correct answer`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val quizState = de.cleema.android.core.models.QuizState(FAKE_QUIZ1, streak = 42, answer = null)
        quizRepository.quizChannel.send(Result.success(quizState))

        assertEquals(
            Pending(
                question = quizState.quiz.question,
                choices = quizState.quiz.choices.toSortedMap(),
                quizId = quizState.quiz.id
            ), sut.uiState.value
        )

        sut.answer(quizState.quiz.correctAnswer)

        assertEquals(quizState.quiz.correctAnswer, quizRepository.answeredChoice)
        assertEquals(quizState.quiz.id, quizRepository.answeredId)

        quizRepository.quizChannel.send(
            Result.success(
                quizState.copy(
                    answer = de.cleema.android.core.models.QuizState.Answer(System.now(), quizState.quiz.correctAnswer)
                )
            )
        )

        assertEquals(
            AnswerQuizUiState.Answered(
                question = quizState.quiz.question,
                choice = quizState.quiz.correctAnswer,
                answer = quizState.quiz.choices[quizState.quiz.correctAnswer]!!,
                explanation = quizState.quiz.explanation,
                isCorrect = true
            ),
            sut.uiState.value
        )

        collectJob.cancel()
    }
}


