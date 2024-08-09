package de.cleema.android.core.components

import kotlinx.datetime.*
import kotlin.time.Duration.Companion.days

val Instant.endOfDay: Instant
    get() = with(toLocalDateTime(TimeZone.UTC)) {
        LocalDateTime(year, monthNumber, dayOfMonth, 23, 59, 59)
    }.toInstant(TimeZone.UTC)

val Instant.startOfDay: Instant
    get() = with(toLocalDateTime(TimeZone.UTC)) {
        LocalDateTime(year, monthNumber, dayOfMonth, 0, 0, 0)
    }.toInstant(TimeZone.UTC)

fun Instant.weeksUntil(other: Instant, timeZone: TimeZone): Int =
    until(other, DateTimeUnit.WEEK, timeZone).toInt()

fun Instant.addWeeks(weeks: Int): Instant = plus((weeks * 7).days)
