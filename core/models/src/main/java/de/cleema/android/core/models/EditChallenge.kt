package de.cleema.android.core.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.*
import kotlin.time.Duration.Companion.days

data class EditChallenge(
    val title: String = "",
    val teaserText: String = "",
    val description: String = "",
    val goalType: de.cleema.android.core.models.Challenge.GoalType = de.cleema.android.core.models.Challenge.GoalType.Steps(
        42
    ),
    val interval: de.cleema.android.core.models.Challenge.Interval = de.cleema.android.core.models.Challenge.Interval.DAILY,
    val start: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val end: LocalDate = Clock.System.now().plus(30.days).toLocalDateTime(TimeZone.UTC).date,
    val isPublic: Boolean = false,
    val regionId: UUID? = null,
    val kind: de.cleema.android.core.models.Challenge.Kind = de.cleema.android.core.models.Challenge.Kind.User,
    val logo: de.cleema.android.core.models.RemoteImage? = null,
    val image: de.cleema.android.core.models.IdentifiedImage? = null
)
