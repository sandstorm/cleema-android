package de.cleema.android.drawer

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.drawer.UserDrawerItemUiState.Content
import de.cleema.android.drawer.UserDrawerItemUiState.Loading
import de.cleema.android.helpers.FAKE_LOCAL
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
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
class UserDrawerItemViewModelTest {
    private lateinit var repository: FakeUserRepository
    private lateinit var sut: UserDrawerItemViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        sut = UserDrawerItemViewModel(repository)
    }

    @Test
    fun `It loads the current user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        assertEquals(Loading, sut.uiState.value)

        val user = FAKE_LOCAL.copy(
            avatar = de.cleema.android.core.models.IdentifiedImage(
                image = de.cleema.android.core.models.RemoteImage(
                    "http://localhorst.de"
                )
            )
        )
        repository.stubbedUserValue = Valid(user)

        assertEquals(
            Content(user), sut.uiState.value
        )

        collectJob.cancel()
    }
}
