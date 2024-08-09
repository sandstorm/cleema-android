package de.cleema.android.navigation

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.helpers.FAKE_LOCAL
import de.cleema.android.helpers.FAKE_REMOTE
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.navigation.AppNavigationUiState.Destination.Drawer
import de.cleema.android.navigation.AppNavigationUiState.Destination.Info
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
class AppNavigationViewModelTest {

    private lateinit var repository: FakeUserRepository
    private lateinit var sut: AppNavigationViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        sut = AppNavigationViewModel(repository)
    }

    private fun expectedInfoContent(isLocal: Boolean = false): List<InfoContent> {
        return DrawerRoute.values().map { InfoContent(it, /*if (it == DrawerRoute.SPONSORSHIP) !isLocal else*/ true) }
    }

    @Test
    fun `Drawer navigation flow`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        repository.stubbedUserValue = Valid(FAKE_REMOTE)

        for (route in DrawerRoute.values()) {
            sut.openNavigationMenu()

            assertEquals(
                AppNavigationUiState(infoDrawerContent = expectedInfoContent(false), destination = Drawer),
                sut.uiState.value
            )

            sut.select(route)

            assertEquals(
                AppNavigationUiState(
                    infoDrawerContent = expectedInfoContent(false), destination = Info(route)
                ),
                sut.uiState.value
            )

            sut.close()

            assertEquals(AppNavigationUiState(infoDrawerContent = expectedInfoContent(false)), sut.uiState.value)
        }

        collectJob.cancel()
    }

    @Test
    fun `Tab navigation`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        repository.stubbedUserValue = Valid(FAKE_REMOTE)

        AppScreens.values().forEach {
            sut.selectScreen(it)

            assertEquals(AppNavigationUiState(it, expectedInfoContent(false), destination = null), sut.uiState.value)

            sut.openNavigationMenu()

            assertEquals(AppNavigationUiState(it, expectedInfoContent(false), destination = Drawer), sut.uiState.value)

            val route = DrawerRoute.values().random()
            sut.select(route)

            assertEquals(
                AppNavigationUiState(it, expectedInfoContent(false), destination = Info(route)),
                sut.uiState.value
            )
        }

        repository.stubbedUserValue = null

        assertEquals(AppScreens.Dashboard, sut.uiState.value.screen)

        collectJob.cancel()
    }

    @Test
    fun `Profile navigation will reset the drawer route`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        sut.openProfile()

        assertEquals(AppNavigationUiState(destination = AppNavigationUiState.Destination.Profile), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Info drawer values for local users`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        repository.stubbedUserValue = Valid(FAKE_LOCAL)

        assertEquals(expectedInfoContent(true), sut.uiState.value.infoDrawerContent)

        collectJob.cancel()
    }
}
