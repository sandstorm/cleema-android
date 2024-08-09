package de.cleema.android.dashboard

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.dashboard.DashboardItem.*
import de.cleema.android.helpers.FAKE_REMOTE
import de.cleema.android.helpers.FakeSurveysRepository
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
class DashboardViewModelTest {

    private lateinit var surveysRepository: FakeSurveysRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var sut: DashboardViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun givenUserAcceptsSurveys(acceptsSurveys: Boolean, surveyCount: Int) {
        val user = FAKE_REMOTE.copy(acceptsSurveys = acceptsSurveys)
        userRepository.stubbedUserValue = Valid(user)
        surveysRepository.givenSurveys(surveyCount)
    }

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        surveysRepository = FakeSurveysRepository()
        sut = DashboardViewModel(userRepository = userRepository, surveysRepository)
    }

    @Test
    fun `It has surveys for users who accept surveys`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUserAcceptsSurveys(true, 10)

        assertEquals(DashboardUiState.Content(DashboardItem.values().toList()), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `It has no surveys for users who do not accept surveys`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUserAcceptsSurveys(false, 10)

        assertEquals(DashboardUiState.Content(listOf(SocialAndProjects, Quiz, Challenges)), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `It has no surveys for users who accept surveys when the surveys repository is empty`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUserAcceptsSurveys(true, 0)

        assertEquals(DashboardUiState.Content(listOf(SocialAndProjects, Quiz, Challenges)), sut.uiState.value)

        collectJob.cancel()
    }
}
