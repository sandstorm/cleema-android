package de.cleema.android.partnerchallenges

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.models.Challenge
import de.cleema.android.core.models.Partner
import de.cleema.android.helpers.FakeChallengesRepository
import de.cleema.android.helpers.FakeUrlOpener
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.partnerchallenges.PartnerChallengeUiState.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class PartnerChallengeViewModelTests {
    private lateinit var opener: FakeUrlOpener
    private lateinit var savedState: SavedStateHandle
    private lateinit var repository: FakeChallengesRepository
    private lateinit var sut: PartnerChallengeViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun givenNoChallenge(id: UUID) {
        repository.challengeMap.clear()
        savedState["challengeId"] = id.toString()
    }

    private fun given(challenge: de.cleema.android.core.models.Challenge) {
        repository.challengeMap[challenge.id] = challenge
        savedState["challengeId"] = challenge.id.toString()
    }

    @Before
    fun setUp() {
        repository = FakeChallengesRepository()
        savedState = SavedStateHandle()
        opener = FakeUrlOpener()
        sut = PartnerChallengeViewModel(repository, savedState, opener)
    }

    @Test
    fun `Loading challenges`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        assertEquals(Loading, sut.uiState.value)

        val challenge = de.cleema.android.core.models.Challenge.of(title = "Challenge")
        given(challenge)

        assertEquals(Success(challenge), sut.uiState.value)

        val id = UUID.randomUUID()
        givenNoChallenge(id)

        assertEquals(NotFound("No challenge with $id found"), sut.uiState.value)

        val c = de.cleema.android.core.models.Challenge.of(title = "Another challenge")
        given(c)

        assertEquals(Success(c), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Leaving a challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val challenge = de.cleema.android.core.models.Challenge.of(title = "Challenge", joined = true)
        given(challenge)

        assertEquals(Success(challenge, false), sut.uiState.value)

        sut.socialButtonTapped()

        assertEquals(Success(challenge, true), sut.uiState.value)
        assertNull(repository.leaveChallengeId)

        sut.dismissAlert()

        assertEquals(Success(challenge, false), sut.uiState.value)
        assertNull(repository.leaveChallengeId)

        sut.confirmLeave()

        assertEquals(Success(challenge, false), sut.uiState.value)
        assertEquals(challenge.id, repository.leaveChallengeId)

        collectJob.cancel()
    }

    @Test
    fun `Joining a challenge`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val challenge = de.cleema.android.core.models.Challenge.of(title = "Challenge", joined = false)
        given(challenge)

        assertEquals(Success(challenge, false), sut.uiState.value)

        sut.socialButtonTapped()

        assertEquals(Success(challenge, false), sut.uiState.value)
        assertEquals(challenge.id, repository.joinedChallengeId)

        collectJob.cancel()
    }

    @Test
    fun `Clicking on a partner will open its url`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val partner = de.cleema.android.core.models.Partner(url = "http://cleema.app/partnerlink")
        val challenge = de.cleema.android.core.models.Challenge.of(title = "Challenge", joined = false, kind = de.cleema.android.core.models.Challenge.Kind.Partner(partner))
        given(challenge)

        assertEquals(Success(challenge, false), sut.uiState.value)

        sut.partnerTapped()

        assertEquals("http://cleema.app/partnerlink", opener.invokedUri)

        collectJob.cancel()
    }
}
