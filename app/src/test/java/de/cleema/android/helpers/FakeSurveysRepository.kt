package de.cleema.android.helpers

import de.cleema.android.core.data.SurveysRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*
import kotlin.random.Random

class FakeSurveysRepository : SurveysRepository {
    val stubbedSurveys = Channel<Result<List<de.cleema.android.core.models.Survey>>>()

    fun givenSurveys(count: Int): List<de.cleema.android.core.models.Survey> {
        val surveys =
            (1..count).map {
                de.cleema.android.core.models.Survey(
                    title = "Survey $it",
                    description = "Description $it",
                    state = if (Random.nextBoolean()) de.cleema.android.core.models.SurveyState.Participation("participation $it") else de.cleema.android.core.models.SurveyState.Evaluation(
                        "evaluation $it"
                    )
                )
            }
        stubbedSurveys.trySend(Result.success(surveys))
        return surveys
    }

    override fun getSurveysStream(): Flow<Result<List<de.cleema.android.core.models.Survey>>> =
        stubbedSurveys.receiveAsFlow()

    override suspend fun evaluate(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun participate(id: UUID) {
        TODO("Not yet implemented")
    }
}
