package de.cleema.android.core.models

import de.cleema.android.core.models.Challenge.Interval.DAILY
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toInstant
import org.junit.Test

class JoinedChallengeTests {
    @Test
    fun `Daily challenge`() {
        val start = LocalDateTime(2022, 7, 1, 0, 0)
        val end = LocalDateTime(2022, 7, 31, 0, 0)
        val sut = JoinedChallenge(
            Challenge.of(
                title = "Challenge",
                interval = DAILY,
                startDate = start.toInstant(UTC),
                endDate = end.toInstant(UTC)
            ),
            (1..11).associateWith { JoinedChallenge.Answer.SUCCEEDED }
        )

        assertEquals(11.0 / 31.0, sut.progress)
    }
}
