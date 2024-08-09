/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.regions

import de.cleema.android.core.data.UserValue
import de.cleema.android.helpers.FakeRegionsRepository
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class RegionsViewModelTest {
    private lateinit var userRepository: FakeUserRepository
    private lateinit var repository: FakeRegionsRepository
    private lateinit var sut: RegionsViewModel

    val LEIPZIG = de.cleema.android.core.models.Region(name = "Leipzig")
    val DRESDEN = de.cleema.android.core.models.Region(name = "Dresden")
    val PIRNA = de.cleema.android.core.models.Region(name = "Pirna")

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeRegionsRepository()
        userRepository = FakeUserRepository()
        sut = RegionsViewModel(repository, userRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `It loads the initial selection from the user repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val user = de.cleema.android.core.models.User(
            id = UUID.randomUUID(),
            name = "user",
            region = DRESDEN,
            joinDate = Instant.DISTANT_FUTURE,
            isSupporter = false
        )
        userRepository.stubbedUserValue = UserValue.Valid(user)
        repository.regions = listOf(LEIPZIG, DRESDEN)

        assertEquals(
            RegionsUiState.Success(listOf(LEIPZIG, DRESDEN), selection = user.region),
            sut.uiState.value
        )

        sut.select(LEIPZIG.id)

        assertEquals(
            RegionsUiState.Success(listOf(LEIPZIG, DRESDEN), selection = LEIPZIG),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `The initial selection is null when the user repository has no user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = null
        repository.regions = listOf(LEIPZIG, DRESDEN)

        assertEquals(
            RegionsUiState.Success(listOf(LEIPZIG, DRESDEN), selection = null),
            sut.uiState.value
        )

        collectJob.cancel()
    }
}
