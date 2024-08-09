package de.cleema.android.profile

import de.cleema.android.core.models.SocialGraph
import de.cleema.android.core.models.SocialGraphItem
import de.cleema.android.core.models.SocialUser
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
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

@OptIn(ExperimentalCoroutinesApi::class)
class SocialGraphViewModelTest {
    private lateinit var sut: SocialGraphViewModel
    private lateinit var repository: FakeUserRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        sut = SocialGraphViewModel(repository)
    }

    @Test
    fun `It loads the social which arent only requests`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(SocialGraphUiState.Loading, sut.uiState.value)

        val graph = de.cleema.android.core.models.SocialGraph(
            followers = listOf(
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "A")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = true,
                    user = de.cleema.android.core.models.SocialUser(username = "B")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "C")
                ),
            ),
            following = listOf(
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = true,
                    user = de.cleema.android.core.models.SocialUser(username = "D")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = false,
                    user = de.cleema.android.core.models.SocialUser(username = "E")
                ),
                de.cleema.android.core.models.SocialGraphItem(
                    isRequest = true,
                    user = de.cleema.android.core.models.SocialUser(username = "F")
                ),
            )
        )
        repository.stub(Result.success(graph))

        assertEquals(
            SocialGraphUiState.Content(
                followers = graph.followers.filterNot { it.isRequest },
                following = graph.following.filterNot { it.isRequest }),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Deleting an user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(SocialGraphUiState.Loading, sut.uiState.value)

        val users = listOf(
            de.cleema.android.core.models.SocialUser(username = "A"),
            de.cleema.android.core.models.SocialUser(username = "B")
        )
        val graph = de.cleema.android.core.models.SocialGraph(
            followers = listOf(
                de.cleema.android.core.models.SocialGraphItem(isRequest = false, user = users[0]),
            ),
            following = listOf(
                de.cleema.android.core.models.SocialGraphItem(isRequest = false, user = users[1])
            )
        )
        repository.stub(Result.success(graph))

        assertEquals(
            SocialGraphUiState.Content(
                followers = graph.followers,
                following = graph.following
            ),
            sut.uiState.value
        )

        sut.onRemoveUserClick(graph.followers[0].id)

        assertEquals(
            SocialGraphUiState.Content(
                followers = graph.followers,
                following = graph.following,
                alertItem = graph.followers[0]
            ),
            sut.uiState.value
        )

        sut.dismissAlert()

        assertEquals(
            SocialGraphUiState.Content(
                followers = graph.followers,
                following = graph.following,
                alertItem = null
            ),
            sut.uiState.value
        )
        assertNull(repository.unfollowedId)

        sut.onRemoveUserClick(graph.following[0].id)

        assertEquals(
            SocialGraphUiState.Content(
                followers = graph.followers,
                following = graph.following,
                alertItem = graph.following[0]
            ),
            sut.uiState.value
        )

        sut.confirmUnfollowing()

        assertEquals(
            SocialGraphUiState.Content(
                followers = graph.followers,
                following = graph.following,
                alertItem = null
            ),
            sut.uiState.value
        )
        assertEquals(graph.following[0].id, repository.unfollowedId)

        collectJob.cancel()
    }
}
