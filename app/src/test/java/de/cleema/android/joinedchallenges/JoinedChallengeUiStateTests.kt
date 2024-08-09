package de.cleema.android.joinedchallenges

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.datetime.LocalDate
import org.junit.Test
import kotlin.random.Random

class JoinedChallengeUiStateTests {
    @Test
    fun `Derived properties of Status`() {
        assertFalse(JoinedChallengeUiState.Value.Status.Answered.isPending)
        assertFalse(JoinedChallengeUiState.Value.Status.Expired.isPending)
        assertFalse(JoinedChallengeUiState.Value.Status.Upcoming(Random.nextInt()).isPending)
        assertTrue(JoinedChallengeUiState.Value.Status.Pending(Random.nextInt(), LocalDate(1, 1, 1)).isPending)
        assertTrue(JoinedChallengeUiState.Value.Status.PendingWeekly(Random.nextInt(), Random.nextInt()).isPending)
    }
}
