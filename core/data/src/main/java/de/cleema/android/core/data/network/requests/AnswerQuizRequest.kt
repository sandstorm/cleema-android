package de.cleema.android.core.data.network.requests

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.core.data.network.responses.QuizAnswer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AnswerQuizRequest(
    @Serializable(with = UUIDSerializer::class)
    val quiz: UUID,
    val answer: QuizAnswer
)
