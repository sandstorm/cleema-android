package de.cleema.android.joinedchallenges

import de.cleema.android.core.models.Challenge
import de.cleema.android.core.models.JoinedChallenge
import de.cleema.android.helpers.FakeChallengesRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.joinedchallenges.JoinedChallengesUiState.Content
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JoinedChallengesViewModelTest {
    private lateinit var repository: FakeChallengesRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var sut: JoinedChallengesViewModel

    @Before
    fun setUp() {
        repository = FakeChallengesRepository()
        sut = JoinedChallengesViewModel(repository)
    }

    @Test
    fun `It loads the challenges`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        assertEquals(Content(isLoading = true), sut.uiState.value)

        val challenges = listOf(
            JoinedChallenge(Challenge.of(title = "c1")),
            JoinedChallenge(Challenge.of(title = "c2"))
        )
        repository.stubJoinedChallenges(challenges)

        assertEquals(Content(challenges, false), sut.uiState.value)

        collectJob.cancel()
    }
}
