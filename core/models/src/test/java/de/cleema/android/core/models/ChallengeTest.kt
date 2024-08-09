package de.cleema.android.core.models

import de.cleema.android.core.components.endOfDay
import de.cleema.android.core.components.startOfDay
import de.cleema.android.core.models.Challenge.Duration.Days
import de.cleema.android.core.models.Challenge.Duration.Weeks
import de.cleema.android.core.models.Challenge.Interval.DAILY
import de.cleema.android.core.models.Challenge.Interval.WEEKLY
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant
import org.junit.Test
import kotlin.time.Duration.Companion.days

class ChallengeTest {
    @Test
    fun `Very long daily challenge`() {
        val start = LocalDateTime(2021, 12, 1, 0, 0)
        val end = LocalDateTime(2023, 3, 12, 0, 0)
        val sut = Challenge.of(
            title = "Challenge",
            interval = DAILY,
            startDate = start.toInstant(UTC),
            endDate = end.toInstant(UTC)
        )

        assertEquals(Days(467), sut.duration)
    }

    @Test
    fun `Durartion of a daily challenge`() {
        val start =
            LocalDateTime(
                year = 2021,
                monthNumber = 12,
                dayOfMonth = 1,
                hour = 0,
                minute = 0
            ).toInstant(
                UTC
            )

        for (dayIndex in (0..365)) {
            val sut = Challenge.of(
                title = "Challenge",
                interval = DAILY,
                startDate = start,
                endDate = start.plus(dayIndex.days).endOfDay
            )

            assertEquals(Days(dayIndex + 1), sut.duration)
        }
    }

    @Test
    fun `Weekly with end lesser than seven days from start will set the end date one week after the start date`() {
        val start = LocalDateTime(2022, 8, 1, 9, 41).toInstant(UTC)

        val sut = Challenge.of(
            title = "Challenge",
            interval = WEEKLY,
            startDate = start,
            endDate = start.plus(3.days)
        )

        assertEquals(Weeks(1), sut.duration)
    }

    @Test
    fun `Start and end date of a challenge will be set to begin and end of day`() {
        val start = LocalDateTime(2022, 1, 1, 10, 28, 23).toInstant(UTC)
        val end = LocalDateTime(2022, 1, 1, 18, 42, 12).toInstant(UTC)

        val sut = Challenge.of(title = "Challenge", startDate = start, endDate = end)

        assertEquals(
            LocalDateTime(2022, 1, 1, 0, 0, 0, nanosecond = 0).toInstant(UTC),
            sut.startDate
        )
        assertEquals(LocalDateTime(2022, 1, 1, 23, 59, 59).toInstant(UTC), sut.endDate)
    }

    @Test
    fun `Duration for weekly challenge`() {
        val start =
            LocalDateTime(
                year = 2021,
                monthNumber = 12,
                dayOfMonth = 1,
                hour = 0,
                minute = 0
            ).toInstant(
                UTC
            )

        for (weekIndex in (0..15)) {
            val sut = Challenge.of(
                title = "Challenge",
                interval = WEEKLY,
                startDate = start,
                endDate = start.plus(weekIndex.days * 7)
            )

            assertEquals(Weeks(weekIndex + 1), sut.duration)
        }
    }

    @Test
    fun `Very long weekly challenge`() {
        val start = LocalDateTime(2021, 12, 1, 9, 41).toInstant(UTC)
        val end = LocalDateTime(2023, 3, 12, 9, 41).toInstant(UTC)

        val sut = Challenge.of(
            title = "Challenge",
            interval = WEEKLY,
            startDate = start,
            endDate = end
        )

        assertEquals(Weeks(67), sut.duration)
    }

    @Test
    fun `End date is always after start date for daily challenges`() {
        val start = LocalDateTime(2022, 1, 1, 9, 41).toInstant(UTC)
        val end = start.plus((-10).days)

        val daily = Challenge.of(
            title = "Challenge",
            interval = DAILY,
            startDate = start,
            endDate = end
        )
        assertEquals(end.startOfDay, daily.startDate)
        assertEquals(start.endOfDay, daily.endDate)

        val weekly = Challenge.of(
            title = "Challenge",
            interval = WEEKLY,
            startDate = start,
            endDate = end
        )

        assertEquals(end.startOfDay, weekly.startDate)
        assertEquals(start.endOfDay, weekly.endDate)
    }
}
