package de.cleema.android.createchallenge

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.EditChallenge
import de.cleema.android.createchallenge.CreateChallengeUiState.Edit
import de.cleema.android.helpers.*
import de.cleema.android.shared.CurrentUserUseCase
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
class CreateChallengeViewModelTest {
    private lateinit var userRepository: FakeUserRepository
    private lateinit var sut: CreateChallengeViewModel
    private lateinit var repository: FakeChallengesRepository


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        repository = FakeChallengesRepository()
        sut = CreateChallengeViewModel(repository, CurrentUserUseCase(userRepository))
    }

    @Test
    fun `Choosing and editing a template for a local user will save it with the users region id`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = Valid(FAKE_LOCAL)
        repository.createChallengeResult =
            Result.success(de.cleema.android.core.models.JoinedChallenge(de.cleema.android.core.models.Challenge.of()))

        assertEquals(CreateChallengeUiState.ChooseTemplate, sut.uiState.value)

        val template = EditChallenge()

        sut.edit(template)

        assertEquals(Edit(template), sut.uiState.value)

        val expected = template.copy(title = "Edited title", description = "updated description")
        sut.edit(expected)

        assertEquals(Edit(expected), sut.uiState.value)

        sut.nextClicked()

        assertEquals(expected.copy(regionId = FAKE_LOCAL.region.id), repository.createdChallenge)
        assertEquals(setOf<UUID>(), repository.createdParticipants)
        assertEquals(CreateChallengeUiState.Done, sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Choosing and editing a template for remote user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = Valid(FAKE_REMOTE)
        repository.createChallengeResult =
            Result.success(de.cleema.android.core.models.JoinedChallenge(de.cleema.android.core.models.Challenge.of()))

        assertEquals(CreateChallengeUiState.ChooseTemplate, sut.uiState.value)

        val template = EditChallenge()

        sut.edit(template)

        assertEquals(Edit(template, canInviteFriends = true), sut.uiState.value)

        sut.nextClicked()

        assertNull(repository.createdChallenge)
        assertNull(repository.createdParticipants)
        assertEquals(CreateChallengeUiState.InviteUsers(template), sut.uiState.value)

        val selection = setOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        sut.inviteUsersWithIds(selection)

        assertEquals(CreateChallengeUiState.InviteUsers(template, selection), sut.uiState.value)

        sut.nextClicked()

        assertEquals(template.copy(regionId = FAKE_REMOTE.region.id), repository.createdChallenge)
        assertEquals(selection, repository.createdParticipants)
        assertEquals(CreateChallengeUiState.Done, sut.uiState.value)
        collectJob.cancel()
    }
}
