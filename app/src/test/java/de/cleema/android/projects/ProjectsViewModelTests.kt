/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.Goal.Involvement
import de.cleema.android.helpers.*
import de.cleema.android.projects.list.ProjectsUiState.*
import de.cleema.android.projects.list.ProjectsViewModel
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
class ProjectsViewModelTests {
    private lateinit var repository: FakeProjectsRepository
    private lateinit var userRepository: FakeUserRepository
    private lateinit var sut: ProjectsViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeProjectsRepository()
        userRepository = FakeUserRepository()
        sut = ProjectsViewModel(repository, userRepository)
    }

    @Test
    fun `Filtering projects`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)

        userRepository.stubbedUserValue = Valid(FAKE_LOCAL)

        val projects = listOf(
            de.cleema.android.core.models.Project(
                title = "t1",
                partner = FAKE_PARTNER,
                region = FAKE_LOCAL.region,
                goal = de.cleema.android.core.models.Goal.Information
            ),
            de.cleema.android.core.models.Project(
                title = "t2",
                partner = FAKE_PARTNER,
                region = DRESDEN,
                goal = Involvement(21, 42, true)
            )
        )

        repository.stubProjects(FAKE_LOCAL.region.id, projects)

        assertEquals(Content(projects), sut.uiState.value)

        repository.givenFailure(FAKE_LOCAL.region.id)

        assertEquals(Error("No projects for id"), sut.uiState.value)

        val other = listOf(
            de.cleema.android.core.models.Project(
                title = "o1",
                partner = FAKE_PARTNER,
                region = PIRNA,
                goal = de.cleema.android.core.models.Goal.Information
            ),
            de.cleema.android.core.models.Project(
                title = "o2", partner = FAKE_PARTNER, region = PIRNA, goal = Involvement(47, 11, false)
            )
        )
        repository.stubProjects(PIRNA.id, other)

        sut.setRegion(PIRNA.id)

        assertEquals(Content(other), sut.uiState.value)

        collectJob.cancel()
    }
}
