package de.cleema.android.joinedchallenges

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.components.addWeeks
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.Challenge.Interval.WEEKLY
import de.cleema.android.core.models.JoinedChallenge.Answer.FAILED
import de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED
import de.cleema.android.core.models.duration
import de.cleema.android.helpers.*
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.*
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.Value.Status
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.Value.Status.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone.Companion.UTC
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.random.Random.Default.nextBoolean
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class)
class JoinedChallengeViewModelTest {
    private lateinit var userRepository: FakeUserRepository
    private lateinit var savedState: SavedStateHandle
    private lateinit var repository: FakeChallengesRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var sut: JoinedChallengeViewModel

    private fun givenNoChallenge(id: UUID) {
        savedState["challengeId"] = id.toString()
        repository.joinedChallengeChannel.trySend(Result.failure(IllegalArgumentException("No challenge with $id found")))
    }

    private fun given(challenge: de.cleema.android.core.models.JoinedChallenge, onDay: Instant? = null) {
        onDay?.let {
            today = it
        }
        joinedChallenge = challenge
        savedState["challengeId"] = challenge.challenge.id.toString()
        repository.joinedChallengeChannel.trySend(Result.success(challenge))
    }

    private lateinit var joinedChallenge: de.cleema.android.core.models.JoinedChallenge
    private lateinit var today: Instant
    private fun expect(status: Status) {
        assertEquals(Value(joinedChallenge, status), sut.uiState.value)
    }

    private fun answeredOnDays(days: Collection<Int> = setOf()): Map<Int, de.cleema.android.core.models.JoinedChallenge.Answer> =
        days.associateWith { if (nextBoolean()) SUCCEEDED else FAILED }

    private fun nextAnswer(): de.cleema.android.core.models.JoinedChallenge.Answer =
        if (nextBoolean()) SUCCEEDED else FAILED

    @Before
    fun setUp() {
        repository = FakeChallengesRepository()
        savedState = SavedStateHandle()
        today = Clock.System.now()
        userRepository = FakeUserRepository()
        userRepository.stubbedUserValue = Valid(FAKE_LOCAL)
        sut = JoinedChallengeViewModel(repository, userRepository, savedState) { today }
    }

    @Test
    fun `It loads the challenge from the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        assertEquals(Loading, sut.uiState.value)

        givenNoChallenge(UUID.randomUUID())
        assertEquals(NotJoined, sut.uiState.value)

        val join = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today
            ), answers = mapOf()
        )
        given(join)

        expect(Pending(1, today.toLocalDateTime(UTC).date))

        collectJob.cancel()
    }

    @Test
    fun `It calculates the answering status for a daily challenge within the challenge duration`() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
            val joined = de.cleema.android.core.models.JoinedChallenge(
                de.cleema.android.core.models.Challenge.of(
                    title = "Challenge",
                    startDate = today,
                    endDate = today.plus(31.days)
                ),
                answers = mapOf()
            )

            // 30 days challenge duration plus 3 days afterwards for answering
            for (day in (0..30)) {
                given(joined, joined.challenge.startDate.plus(day.days))

                expect(Pending(day + 1, today.toLocalDateTime(UTC).date))
            }

            collectJob.cancel()
        }

    @Test
    fun `It allows three additional days to answer an exceeded challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today,
                endDate = today.plus(31.days)
            ),
            answers = mapOf()
        )

        val valueCount = joined.challenge.duration.valueCount
        for (day in valueCount + 1..valueCount + 2) {
            given(joined, joined.challenge.startDate.plus(day.days))

            expect(Pending(valueCount, joined.challenge.endDate.toLocalDateTime(UTC).date))
        }

        for (day in valueCount + 3..valueCount + 100) {
            given(joined, joined.challenge.startDate.plus(day.days))
            expect(Expired)
        }

        collectJob.cancel()
    }

    @Test
    fun `It is upcoming before the start date of a challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today,
                endDate = today.plus(31.days)
            ),
            answers = mapOf()
        )

        given(joined, joined.challenge.startDate.minus(1.days))
        expect(Upcoming(1))

        given(joined, joined.challenge.startDate.minus(2.days))
        expect(Upcoming(2))

        given(joined, joined.challenge.startDate.minus(42.days))
        expect(Upcoming(42))

        collectJob.cancel()
    }

    @Test
    fun `Validation of a daily challenge with answers with answer every day`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val startDate = today
        val endDate = startDate.plus(31.days)

        for (day in 0..startDate.daysUntil(endDate, UTC)) {
            val joined = de.cleema.android.core.models.JoinedChallenge(
                de.cleema.android.core.models.Challenge.of(startDate = startDate, endDate = endDate),
                answers = answeredOnDays((1..day + 1).toList())
            )

            given(joined, startDate.plus(day.days))
            expect(Answered)
        }

        collectJob.cancel()
    }

    @Test
    fun `Validation of a daily challenge without answering every day`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val startDate = today
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today,
                endDate = today.plus(10.days)
            ),
            answers = answeredOnDays(listOf(1, 2, 5, 8, 10))
        )
        val expected = mapOf(
            1 to Answered,
            2 to Answered,
            3 to Pending(3, joined.challenge.startDate.plus(2.days).toLocalDateTime(UTC).date),
            4 to Pending(4, joined.challenge.startDate.plus(3.days).toLocalDateTime(UTC).date),
            5 to Pending(4, joined.challenge.startDate.plus(3.days).toLocalDateTime(UTC).date),
            6 to Pending(6, joined.challenge.startDate.plus(5.days).toLocalDateTime(UTC).date),
            7 to Pending(7, joined.challenge.startDate.plus(6.days).toLocalDateTime(UTC).date),
            8 to Pending(7, joined.challenge.startDate.plus(6.days).toLocalDateTime(UTC).date),
            9 to Pending(9, joined.challenge.startDate.plus(8.days).toLocalDateTime(UTC).date),
            10 to Pending(9, joined.challenge.startDate.plus(8.days).toLocalDateTime(UTC).date)
        )

        expected.forEach { (day, status) ->
            given(joined, startDate.plus((day - 1).days))
            expect(status)
        }

        collectJob.cancel()
    }

    @Test
    fun `Validation on last day of a daily challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val start = LocalDate(2022, 11, 25).atStartOfDayIn(UTC)
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(startDate = start, endDate = start.plus(5.days)),
            answers = answeredOnDays(listOf(2, 3, 5, 6))
        )
        given(joined, LocalDateTime(2022, 11, 30, 10, 41).toInstant(UTC))

        expect(Pending(4, start.plus(3.days).toLocalDateTime(UTC).date))
        collectJob.cancel()
    }

    @Test
    fun `Validation after last day of a daily challenge with all days answered`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val start = LocalDate(2022, 11, 1).atStartOfDayIn(UTC)
        val end = LocalDateTime(2022, 11, 30, 9, 41).toInstant(UTC)
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(startDate = start, endDate = end),
            answers = answeredOnDays(listOf(26, 27, 28, 29, 30))
        )
        given(joined, LocalDateTime(2022, 12, 1, 17, 0).toInstant(UTC))

        expect(Expired)
        collectJob.cancel()
    }

    @Test
    fun `Answering challenges within the duration`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val challenge = de.cleema.android.core.models.Challenge.of(
            title = "Challenge",
            startDate = today,
            endDate = today.plus(31.days)
        )

        for (day in 0.rangeTo(challenge.startDate.daysUntil(challenge.endDate, UTC))) {
            val answers = answeredOnDays((0..day).toList())
            given(de.cleema.android.core.models.JoinedChallenge(challenge, answers), challenge.startDate.plus(day.days))

            val answer = nextAnswer()
            sut.answer(answer)

            val expected = challenge.id to answers.plus(day.inc() to answer)
            assertEquals(expected, repository.answers)

            given(
                de.cleema.android.core.models.JoinedChallenge(challenge, expected.second),
                challenge.startDate.plus(day.days)
            )
            expect(Answered)

            repository.answers = null

            sut.answer(nextAnswer())
            assertNull(repository.answers)
        }

        collectJob.cancel()
    }

    @Test
    fun `Answering before the start of a challenge does nothing`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today,
                endDate = today.plus(31.days)
            ),
            answers = mapOf()
        )

        for (day in 1..10) {
            given(joined, joined.challenge.startDate.minus(day.days))
            expect(Upcoming(day))

            sut.answer(nextAnswer())

            assertNull(repository.answers)
        }

        collectJob.cancel()
    }

    @Test
    fun `Answering after the end plus three days of a challenge does nothing`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today,
                endDate = today.plus(31.days)
            ),
            answers = mapOf()
        )

        for (day in 4..10) {
            given(joined, joined.challenge.endDate.plus(day.days))
            expect(Expired)

            sut.answer(nextAnswer())

            assertNull(repository.answers)
        }

        collectJob.cancel()
    }

    @Test
    fun `Answering within three days after the end of a challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val start = LocalDate(2022, 10, 1).atStartOfDayIn(UTC)
        val end = LocalDateTime(2022, 10, 31, 9, 41).toInstant(UTC)
        val joined = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(title = "Challenge", startDate = start, endDate = end),
            answers = mapOf()
        )

        for (day in 1..3) {
            given(joined, joined.challenge.endDate.plus(day.days))
            expect(Pending(31, joined.challenge.endDate.toLocalDateTime(UTC).date))

            val answer = nextAnswer()
            sut.answer(answer)

            val expected = joined.challenge.id to mapOf(31 to answer)
            assertEquals(expected, repository.answers)
        }

        collectJob.cancel()
    }

    @Test
    fun `Weekly challenges before the start date`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val start = LocalDate(2023, 1, 1).atStartOfDayIn(UTC)
        val end = LocalDateTime(2023, 1, 31, 9, 41).toInstant(UTC)
        today = start
        val challenge = de.cleema.android.core.models.Challenge.of(
            title = "Challenge",
            startDate = start,
            endDate = end,
            interval = WEEKLY
        )

        for (day in -10..-1) {
            given(de.cleema.android.core.models.JoinedChallenge(challenge), start.plus(day.days))
            expect(Upcoming(-day))
        }

        collectJob.cancel()
    }

    @Test
    fun `Weekly challenges throughout the challenge duration`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val start = LocalDate(2023, 1, 1).atStartOfDayIn(UTC)
        val end = LocalDateTime(2023, 1, 31, 9, 41).toInstant(UTC)
        today = start
        val challenge = de.cleema.android.core.models.Challenge.of(
            title = "Challenge",
            startDate = start,
            endDate = end,
            interval = WEEKLY
        )

        given(de.cleema.android.core.models.JoinedChallenge(challenge), today)
        expect(PendingWeekly(1, 1))

        // following week does not change status
        for (day in 1..6) {
            given(de.cleema.android.core.models.JoinedChallenge(challenge), start.plus(day.days))
            expect(PendingWeekly(1, 1))
        }

        // next week
        given(de.cleema.android.core.models.JoinedChallenge(challenge), start.plus(7.days))
        expect(PendingWeekly(2, 2))

        // answered
        given(
            de.cleema.android.core.models.JoinedChallenge(challenge, answers = mapOf(2 to SUCCEEDED)),
            start.plus(7.days)
        )
        expect(PendingWeekly(1, 2))

        // no change in fetch on next three days
        for (day in 7..9) {
            given(
                de.cleema.android.core.models.JoinedChallenge(challenge, answers = mapOf(2 to SUCCEEDED)),
                start.plus(day.days)
            )
            expect(PendingWeekly(1, 2))
        }

        // after three days in the following week it is answered
        given(
            de.cleema.android.core.models.JoinedChallenge(challenge, answers = mapOf(2 to SUCCEEDED)),
            start.plus(10.days)
        )
        expect(Answered)

        // move forward to 3rd week
        given(
            de.cleema.android.core.models.JoinedChallenge(challenge, answers = mapOf(2 to SUCCEEDED)),
            start.addWeeks(2)
        )
        expect(PendingWeekly(3, 3))

        // 4th week
        given(
            de.cleema.android.core.models.JoinedChallenge(challenge, answers = mapOf(2 to SUCCEEDED, 3 to FAILED)),
            start.addWeeks(3)
        )
        expect(PendingWeekly(4, 4))

        // expired three days after the end of the challenge
        given(
            de.cleema.android.core.models.JoinedChallenge(challenge, answers = mapOf(2 to SUCCEEDED, 3 to FAILED)),
            end.plus(4.days)
        )
        expect(Expired)

        collectJob.cancel()
    }

    @Test
    fun `Leaving a challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val join = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today
            ), answers = mapOf()
        )
        given(join)

        sut.leaveTapped()

        assertEquals(Value(join, Pending(1, today.toLocalDateTime(UTC).date), true), sut.uiState.value)
        assertNull(repository.leaveChallengeId)

        sut.cancelLeaveDialog()

        assertEquals(Value(join, Pending(1, today.toLocalDateTime(UTC).date), false), sut.uiState.value)
        assertNull(repository.leaveChallengeId)

        sut.confirmLeave()

        assertEquals(join.challenge.id, repository.leaveChallengeId)

        collectJob.cancel()
    }


    @Test
    fun `It has the user progress of the friends for group challenges`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = Valid(FAKE_REMOTE)
        val progresses: List<de.cleema.android.core.models.UserProgress> = listOf(
            de.cleema.android.core.models.UserProgress(
                42,
                10,
                de.cleema.android.core.models.SocialUser(username = "Friend 1")
            ),
            de.cleema.android.core.models.UserProgress(
                47,
                11,
                de.cleema.android.core.models.SocialUser(FAKE_REMOTE.id, username = "Myself")
            ),
            de.cleema.android.core.models.UserProgress(
                12,
                7,
                de.cleema.android.core.models.SocialUser(username = "Friend 2")
            ),
        )
        val join = de.cleema.android.core.models.JoinedChallenge(
            de.cleema.android.core.models.Challenge.of(
                title = "Challenge",
                startDate = today,
                kind = de.cleema.android.core.models.Challenge.Kind.Group(progresses)
            ),
            answers = mapOf()
        )
        given(join)

        val expected = progresses.filter { it.user.id != FAKE_REMOTE.id }
        assertEquals(
            Value(
                joinedChallenge = join,
                status = Pending(1, today.toLocalDateTime(UTC).date),
                progresses = expected
            ), sut.uiState.value
        )

        collectJob.cancel()
    }
}
