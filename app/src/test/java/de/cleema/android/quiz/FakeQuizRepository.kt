package de.cleema.android.quiz

import de.cleema.android.core.data.QuizRepository
import de.cleema.android.core.models.Quiz.Choice.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*

val FAKE_QUIZ1: de.cleema.android.core.models.Quiz = de.cleema.android.core.models.Quiz(
    question = "What was first, chicken or egg?",
    choices = mapOf(A to "Chicken", B to "Egg", C to "Fox", D to "Who knows for sure?"),
    correctAnswer = D,
    explanation = "A tough question"
)


class FakeQuizRepository : QuizRepository {
    var answeredId: UUID? = null
    var answeredChoice: de.cleema.android.core.models.Quiz.Choice? = null
    var quizChannel: Channel<Result<de.cleema.android.core.models.QuizState>> = Channel()

    override fun getQuizStream(): Flow<Result<de.cleema.android.core.models.QuizState>> = quizChannel.receiveAsFlow()

    override suspend fun answerQuiz(id: UUID, choice: de.cleema.android.core.models.Quiz.Choice) {
        answeredChoice = choice
        answeredId = id
    }
}
