package de.cleema.android.core.models

import java.util.*

data class Quiz(
    val id: UUID = UUID.randomUUID(),
    val question: String,
    val choices: Map<Choice, String>,
    val correctAnswer: Choice,
    val explanation: String
) {
    enum class Choice {
        A, B, C, D
    }
}
