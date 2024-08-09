/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import de.cleema.android.core.components.endOfDay
import de.cleema.android.core.components.startOfDay
import de.cleema.android.core.models.Challenge.Duration.Days
import de.cleema.android.core.models.Challenge.Duration.Weeks
import de.cleema.android.core.models.Challenge.Interval.DAILY
import de.cleema.android.core.models.Challenge.Interval.WEEKLY
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.WEEK
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.daysUntil
import kotlinx.datetime.until
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

data class Challenge private constructor(
    val id: UUID,
    val title: String,
    val teaserText: String,
    val description: String,
    val type: GoalType,
    val interval: Interval,
    val startDate: Instant,
    val endDate: Instant,
    val isPublic: Boolean,
    val joined: Boolean,
    val kind: Kind,
    val region: Region?,
    val numberOfUsersJoined: Int,
    var image: IdentifiedImage?,
    // TODO: Refactor: Optional for kind "collective" instead of it exiting on all Challenges
    val collectiveGoalAmount: Int?,
    val collectiveProgress: Int?,
) {
    enum class Interval { DAILY, WEEKLY }
    enum class Unit { KILOMETERS, KILOGRAMS }

    sealed interface Duration {
        data class Days(val count: Int) : Duration
        data class Weeks(val count: Int) : Duration

        val valueCount: Int
            get() = when (this) {
                is Days -> count
                is Weeks -> count
            }
        val dayCount: Int
            get() = when (this) {
                is Days -> count
                is Weeks -> count * 7
            }
    }

    sealed interface GoalType {
        data class Steps(val count: Int) : GoalType
        data class Measurement(val count: Int, val unit: Unit = Unit.KILOMETERS) :
            GoalType

        val valueCount: Int
            get() = when (this) {
                is Steps -> count
                is Measurement -> count
            }
    }

    sealed interface Kind {
        object User : Kind
        data class Group(val progresses: List<UserProgress>) : Kind
        data class Partner(val partner: de.cleema.android.core.models.Partner) : Kind
        data class Collective(val partner: de.cleema.android.core.models.Partner) : Kind
    }

    companion object {
        fun of(
            id: UUID = UUID.randomUUID(),
            title: String = "",
            teaserText: String = "Some random teaser text.",
            description: String = "",
            type: GoalType = GoalType.Steps(1),
            interval: Interval = DAILY,
            startDate: Instant = Clock.System.now(),
            endDate: Instant = Clock.System.now().plus(31.days),
            isPublic: Boolean = true,
            joined: Boolean = false,
            kind: Kind = Kind.User,
            region: Region? = null,
            numberOfUsersJoined: Int = 0,
            image: IdentifiedImage? = null,
            collectiveGoalAmount: Int? = null,
            collectiveProgress: Int? = null,
        ): Challenge = Challenge(
            id,
            title,
            teaserText,
            description,
            type,
            interval,
            minOf(startDate, endDate).startOfDay,
            maxOf(startDate, endDate).endOfDay,
            isPublic,
            joined,
            kind,
            region,
            numberOfUsersJoined,
            image,
            collectiveGoalAmount,
            collectiveProgress,
        )
    }
}

val Challenge.duration: Challenge.Duration
    get() = when (interval) {
        DAILY -> Days(startDate.daysUntil(endDate, UTC) + 1)
        WEEKLY -> Weeks((startDate.until(endDate, WEEK, UTC) + 1).toInt())
    }

fun Challenge.ordinalFor(date: Instant): Int? {
    if (!(startDate..endDate).contains(date)) return null

    return when (val duration = duration) {
        is Days -> {
            val days = startDate.daysUntil(date, UTC) + 1
            if (days <= duration.count) days else null
        }

        is Weeks -> {
            val weeks = (startDate.until(date, WEEK, UTC) + 1).toInt()
            if (weeks <= duration.count) weeks else null
        }
    }
}

