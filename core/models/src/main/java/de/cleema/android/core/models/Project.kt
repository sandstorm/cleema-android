/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

sealed interface Phase {
    object Pre : Phase
    object Within : Phase
    object Post : Phase
    object Cancelled : Phase
}

data class Project(
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val description: String = "",
    val date: Instant = Clock.System.now(),
    val partner: Partner,
    val region: Region,
    val location: Location? = null,
    val goal: Goal = Goal.Involvement(0, 1, false),
    val teaserImage: RemoteImage? = null,
    val image: RemoteImage? = null,
    val phase: Phase = Phase.Pre,
    val summary: String = "",
    val isFaved: Boolean = false
)

val Project.isJoined: Boolean
    get() = when (goal) {
        is Goal.Funding -> false
        is Goal.Involvement -> goal.joined
        Goal.Information -> false
    }

sealed class Goal {
    object Information : Goal()

    data class Involvement(
        val currentParticipants: Int,
        val maxParticipants: Int,
        val joined: Boolean
    ) : Goal()

    data class Funding(val currentAmount: Int, val totalAmount: Int) : Goal()

    val isJoined: Boolean
        get() {
            return when (this) {
                is Involvement -> joined
                else -> false
            }
        }
}
