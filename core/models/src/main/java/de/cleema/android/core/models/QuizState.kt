package de.cleema.android.core.models

import kotlinx.datetime.Instant

data class QuizState(
    val quiz: Quiz,
    val streak: Int,
    val answer: Answer? = null,
    val maxSuccessStreak: Int = 0,
    val currentSuccessStreak: Int = 0
) {
    data class Answer(val date: Instant, val choice: Quiz.Choice)
}

