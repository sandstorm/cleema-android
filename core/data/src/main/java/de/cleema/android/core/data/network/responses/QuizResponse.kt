package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
enum class QuizAnswer {
    @SerialName("a")
    A,

    @SerialName("b")
    B,

    @SerialName("c")
    C,

    @SerialName("d")
    D
}

@Serializable
data class QuizResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val date: Instant,
    val question: String,
    val correctAnswer: QuizAnswer,
    val explanation: String?,
    val answers: List<QuizAnswerResponse>,
    var response: GivenAnswerResponse?,
    var streak: StreakResponse?
)

@Serializable
data class StreakResponse(
    val participationStreak: Int,
    val maxCorrectAnswerStreak: Int,
    val correctAnswerStreak: Int,
    // Copied over from iOS version, apparently not needed, consult justin for details
    // val createdAt: Instant,
    // val updatedAt: Instant
)

@Serializable
data class GivenAnswerResponse(
    val date: Instant,
    val answer: QuizAnswer,
    // Copied over from iOS version, apparently not needed, consult justin for details
    // @Serializable(with = UUIDSerializer::class)
    // val anonymousUserID: UUID?,
    // @Serializable(with = UUIDSerializer::class)
    // val uuid: UUID
)

@Serializable
data class QuizAnswerResponse(val option: QuizAnswer, val text: String)
