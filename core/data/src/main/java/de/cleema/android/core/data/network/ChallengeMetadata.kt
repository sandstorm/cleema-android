package de.cleema.android.core.data.network

import kotlinx.serialization.Serializable

@Serializable
enum class ApiGoalType { steps, measurement }

@Serializable
enum class ApiChallengeKind { user, partner, group, collective }

@Serializable
enum class ApiChallengeInterval { daily, weekly }

@Serializable
data class ApiSteps(val count: UInt)

@Serializable
data class ApiGoalMeasurement(val value: UInt, val unit: Unit) {
    @Serializable
    enum class Unit { kg, km }
}
