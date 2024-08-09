package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.AnswerQuizResponse
import de.cleema.android.core.data.network.responses.QuizResponse
import java.util.*

interface QuizDataSource {
    suspend fun getQuizState(regionId: UUID?): Result<QuizResponse>
    suspend fun answerQuiz(id: UUID, answer: de.cleema.android.core.models.Quiz.Choice): Result<AnswerQuizResponse>
}
