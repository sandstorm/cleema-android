package de.cleema.android.social

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.models.DeepLinking.invitationCode
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.social.FollowInvitationUiState.FollowedOnRegistration
import de.cleema.android.social.FollowInvitationUiState.Following
import junit.framework.TestCase.assertEquals
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
class FollowInvitationViewModelTest {
    private lateinit var sut: FollowInvitationViewModel
    private lateinit var repository: FakeUserRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        savedStateHandle = SavedStateHandle()
        repository = FakeUserRepository()
        sut = FollowInvitationViewModel(repository, savedStateHandle)
    }

    @Test
    fun `Following an invitation link`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(FollowInvitationUiState.Loading, sut.uiState.value)

        val expectedItem = de.cleema.android.core.models.SocialGraphItem(
            UUID.randomUUID(),
            false,
            de.cleema.android.core.models.SocialUser(UUID.randomUUID(), "user", null)
        )
        repository.stubbedFollowResult = Result.success(expectedItem)
        savedStateHandle[invitationCode] = "8711"

        assertEquals("8711", repository.followedInvitionCode)
        assertEquals(Following(expectedItem), sut.uiState.value)

        repository.followedInvitionCode = null
        val failure = RuntimeException("Something went wrong")
        repository.stubbedFollowResult = Result.failure(failure)
        savedStateHandle[invitationCode] = "1234"

        assertEquals(FollowInvitationUiState.Error("Something went wrong"), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Following an invitation link used by user registration`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(FollowInvitationUiState.Loading, sut.uiState.value)

        repository.stubbedFollowResult = Result.success(null)
        savedStateHandle[invitationCode] = "8711"

        assertEquals("8711", repository.followedInvitionCode)
        assertEquals(FollowedOnRegistration, sut.uiState.value)

        collectJob.cancel()
    }
}
