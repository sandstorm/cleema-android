package de.cleema.android.createchallenge

import de.cleema.android.core.models.EditChallenge
import de.cleema.android.helpers.FakeChallengesRepository
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
class ChallengeTemplatesViewModelTest {
    private lateinit var repository: FakeChallengesRepository
    private lateinit var sut: ChallengeTemplatesViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeChallengesRepository()
        sut = ChallengeTemplatesViewModel(repository)
    }

    @Test
    fun `It fetches the templates`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        assertEquals(ChallengeTemplatesUiState.Loading, sut.uiState.value)

        val templates = listOf(EditChallenge(), EditChallenge(), EditChallenge())
        repository.stubTemplates(templates)

        assertEquals(ChallengeTemplatesUiState.Content(templates), sut.uiState.value)

        collectJob.cancel()
    }
}
