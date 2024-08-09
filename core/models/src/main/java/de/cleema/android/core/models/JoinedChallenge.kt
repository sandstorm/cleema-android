package de.cleema.android.core.models


data class JoinedChallenge(val challenge: Challenge, val answers: Map<Int, Answer> = mapOf()) {
    enum class Answer { FAILED, SUCCEEDED }
}

val JoinedChallenge.progress: Double
    get() {
        if (challenge.duration.valueCount <= 0) {
            return 0.0
        }
        return numberOfUnitsDone.toDouble() / challenge.duration.valueCount
    }

val JoinedChallenge.collectiveProgress: Double
    get() {
        if (challenge.collectiveProgress != null && challenge.collectiveGoalAmount != null) {
            return challenge.collectiveProgress.toDouble() / challenge.collectiveGoalAmount
        }
        return 0.0
    }

val JoinedChallenge.numberOfUnitsDone: Int
    get() = answers.values.count { it == JoinedChallenge.Answer.SUCCEEDED }
