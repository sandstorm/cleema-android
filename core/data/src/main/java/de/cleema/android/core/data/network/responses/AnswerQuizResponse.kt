package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AnswerQuizResponse(
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    val date: Instant,
    val answer: QuizAnswer
)
