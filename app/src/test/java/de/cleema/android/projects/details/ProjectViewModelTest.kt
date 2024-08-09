package de.cleema.android.projects.details

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.models.Goal
import de.cleema.android.core.models.Location
import de.cleema.android.core.models.Project
import de.cleema.android.helpers.*
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
class ProjectViewModelTest {
    private lateinit var mapOpener: FakeLocationOpener
    private lateinit var opener: FakeUrlOpener
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var repository: FakeProjectsRepository
    private lateinit var sut: ProjectViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private suspend fun given(project: de.cleema.android.core.models.Project) {
        savedStateHandle["projectId"] = project.id.toString()
        repository.projectChannel.send(Result.success(project))
    }

    @Before
    fun setUp() {
        repository = FakeProjectsRepository()
        savedStateHandle = SavedStateHandle()
        opener = FakeUrlOpener()
        mapOpener = FakeLocationOpener()
        sut = ProjectViewModel(repository, opener, savedStateHandle, mapOpener)
    }

    @Test
    fun `It loads a project`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(ProjectUiState.Loading, sut.uiState.value)

        val project = de.cleema.android.core.models.Project(partner = FAKE_PARTNER, region = PIRNA)
        given(project)

        assertEquals(ProjectUiState.Success(project), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Clicking on a partner will pass the uri to the url opener`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        val project = de.cleema.android.core.models.Project(partner = FAKE_PARTNER, region = PIRNA)
        given(project)

        sut.onPartnerClicked()

        assertEquals(project.partner.url, opener.invokedUri)

        collectJob.cancel()
    }

    @Test
    fun `Faving a project`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val project = de.cleema.android.core.models.Project(partner = FAKE_PARTNER, region = PIRNA)
        given(project)

        sut.favClicked()

        assertEquals(project.id, repository.invokedFavStatus?.first)
        assertEquals(!project.isFaved, repository.invokedFavStatus?.second)

        collectJob.cancel()
    }

    @Test
    fun `Join clicked for an unjoined project`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val project =
            de.cleema.android.core.models.Project(
                partner = FAKE_PARTNER,
                region = PIRNA,
                goal = de.cleema.android.core.models.Goal.Involvement(0, 1, false)
            )
        given(project)

        sut.joinClicked()

        assertEquals(project.id, repository.joinedId)
        assertNull(repository.leaveId)

        collectJob.cancel()
    }

    @Test
    fun `Join clicked for a joined project`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val project = de.cleema.android.core.models.Project(
            partner = FAKE_PARTNER,
            region = PIRNA,
            goal = de.cleema.android.core.models.Goal.Involvement(0, 1, true)
        )
        given(project)

        sut.joinClicked()

        assertEquals(project.id, repository.leaveId)
        assertNull(repository.joinedId)

        collectJob.cancel()
    }

    @Test
    fun `Opening map urls`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val project = de.cleema.android.core.models.Project(
            partner = FAKE_PARTNER,
            region = PIRNA,
            goal = de.cleema.android.core.models.Goal.Involvement(0, 1, true),
            location = de.cleema.android.core.models.Location.DRESDEN
        )
        given(project)

        sut.onLocationClicked()

        assertEquals(de.cleema.android.core.models.Location.DRESDEN, mapOpener.openedLocation)

        collectJob.cancel()
    }
}
