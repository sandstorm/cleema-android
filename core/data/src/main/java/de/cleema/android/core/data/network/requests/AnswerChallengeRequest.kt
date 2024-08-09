package de.cleema.android.core.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class AnswerChallengeRequest(val answers: List<AnswerRequest>) {
    @Serializable
    enum class Value { succeeded, failed }

    @Serializable
    data class AnswerRequest(val dayIndex: Int, val answer: Value)
}
