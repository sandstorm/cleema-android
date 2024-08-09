package de.cleema.android.core.data

import de.cleema.android.core.data.network.QuizDataSource
import de.cleema.android.core.data.network.responses.QuizResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject


class DefaultQuizRepository @Inject constructor(
    private val quizDataSource: QuizDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : QuizRepository {
    private val broadcaster = de.cleema.android.core.common.BroadCaster<Result<UUID>>()

    override fun getQuizStream(regionId: UUID?): Flow<Result<de.cleema.android.core.models.QuizState>> = flow {
        emit(quizDataSource.getQuizState(regionId).map { it.toQuizState() })
        broadcaster.events.map {
            quizDataSource.getQuizState(regionId).map { it.toQuizState() }
        }.collect(this)
    }.flowOn(ioDispatcher)

    override suspend fun answerQuiz(id: UUID, choice: de.cleema.android.core.models.Quiz.Choice) {
        broadcaster.post(quizDataSource.answerQuiz(id, choice).map { it.uuid })
    }
}

fun QuizResponse.toQuizState(): de.cleema.android.core.models.QuizState {
    return de.cleema.android.core.models.QuizState(
        quiz = toQuiz(),
        streak = streak?.maxCorrectAnswerStreak ?: 0,
        maxSuccessStreak = streak?.maxCorrectAnswerStreak ?: 0,
        currentSuccessStreak = streak?.correctAnswerStreak ?: 0,
        answer = response?.let {
            de.cleema.android.core.models.QuizState.Answer(
                it.date,
                choice = de.cleema.android.core.models.Quiz.Choice.valueOf(it.answer.name)
            )
        }
    )
}

fun QuizResponse.toQuiz(): de.cleema.android.core.models.Quiz {
    return de.cleema.android.core.models.Quiz(
        id = this.uuid,
        question = question,
        explanation = explanation ?: "",
        choices = answers.fold(mutableMapOf()) { acc, answer ->
            acc[de.cleema.android.core.models.Quiz.Choice.valueOf(answer.option.name)] = answer.text
            acc
        },
        correctAnswer = de.cleema.android.core.models.Quiz.Choice.valueOf(correctAnswer.name)
    )
}
