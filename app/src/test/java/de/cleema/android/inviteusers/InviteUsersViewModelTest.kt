package de.cleema.android.inviteusers

import de.cleema.android.core.models.SocialGraph
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.core.models.SocialGraphItem
import de.cleema.android.core.models.SocialUser
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
class InviteUsersViewModelTest {
    private lateinit var sut: InviteUsersViewModel
    private lateinit var repository: FakeUserRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        sut = InviteUsersViewModel(repository)
    }

    @Test
    fun `It loads the accepted followers from the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(InviteUsersUiState.Loading, sut.uiState.value)

        val graph = de.cleema.android.core.models.SocialGraph(
            followers = listOf(
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = true,
                    user = de.cleema.android.core.models.SocialUser(username = "Requested user 1")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "Accepted user 1")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = true,
                    user = de.cleema.android.core.models.SocialUser(username = "Requested user 2")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "Accepted user 2")
                ),
            ), following = listOf()
        )
        repository.stub(Result.success(graph))

        assertEquals(
            InviteUsersUiState.Content(listOf(graph.followers[1].user, graph.followers[3].user), setOf()),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Selecting followers`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val graph = de.cleema.android.core.models.SocialGraph(
            followers = listOf(
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "user 1")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "user 2")
                ),
            ), following = listOf()
        )
        repository.stub(Result.success(graph))

        sut.selectUser(graph.followers[0].user.id)

        assertEquals(
            InviteUsersUiState.Content(graph.followers.map(de.cleema.android.core.models.SocialGraphItem::user), setOf(graph.followers[0].user.id)),
            sut.uiState.value
        )

        sut.selectUser(graph.followers[0].user.id)

        assertEquals(
            InviteUsersUiState.Content(graph.followers.map(de.cleema.android.core.models.SocialGraphItem::user), setOf()),
            sut.uiState.value
        )

        sut.selectUser(graph.followers[1].user.id)

        assertEquals(
            InviteUsersUiState.Content(graph.followers.map(de.cleema.android.core.models.SocialGraphItem::user), setOf(graph.followers[1].user.id)),
            sut.uiState.value
        )

        sut.selectUser(graph.followers[0].user.id)

        assertEquals(
            InviteUsersUiState.Content(
                graph.followers.map(de.cleema.android.core.models.SocialGraphItem::user),
                setOf(graph.followers[0].user.id, graph.followers[1].user.id)
            ),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `It handles errors from the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val error = RuntimeException("Test error")
        repository.stub(Result.failure(error))

        assertEquals(InviteUsersUiState.Error("Test error"), sut.uiState.value)
        
        collectJob.cancel()
    }
}
