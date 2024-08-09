package de.cleema.android.survey

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.*
import de.cleema.android.survey.SurveyUiState.Content
import de.cleema.android.survey.SurveyUiState.Denied
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
class SurveysViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var opener: FakeUrlOpener
    private lateinit var repository: FakeSurveysRepository
    private lateinit var sut: SurveysViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeSurveysRepository()
        opener = FakeUrlOpener()
        userRepository = FakeUserRepository()
        sut = SurveysViewModel(repository, opener, userRepository)
    }

    private fun givenUserAcceptsSurveys(acceptsSurveys: Boolean) {
        val user = FAKE_REMOTE.copy(acceptsSurveys = acceptsSurveys)
        userRepository.stubbedUserValue = Valid(user)
    }

    @Test
    fun `It fetches the surveys for users who accepts surveys`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(SurveyUiState.Loading, sut.uiState.value)
        givenUserAcceptsSurveys(true)

        val surveys = repository.givenSurveys(10)

        assertEquals(Content(surveys), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `It does not fetch the surveys for users who do not accepts surveys`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(SurveyUiState.Loading, sut.uiState.value)
        givenUserAcceptsSurveys(false)

        val surveys = repository.givenSurveys(10)

        assertEquals(Denied, sut.uiState.value)

        collectJob.cancel()
    }
}
