/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.partnerchallenges

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.*
import de.cleema.android.partnerchallenges.PartnerChallengesUiState.Content
import de.cleema.android.shared.RegionForCurrentUserUseCase
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
class PartnerChallengesViewModelTests {
    private lateinit var userRepository: FakeUserRepository
    private lateinit var repository: FakeChallengesRepository
    private lateinit var sut: PartnerChallengesViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeChallengesRepository()
        userRepository = FakeUserRepository()
        sut = PartnerChallengesViewModel(repository, RegionForCurrentUserUseCase(userRepository))
    }

    @Test
    fun `It fetches the challenges of the users region initially`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val challenges = listOf(
            de.cleema.android.core.models.Challenge.of(title = "C1"),
            de.cleema.android.core.models.Challenge.of(title = "t2")
        )
        repository.stubChallenges(PIRNA.id, challenges)
        userRepository.stubbedUserValue = Valid(
            de.cleema.android.core.models.User(
                name = "User",
                region = PIRNA,
                isSupporter = false
            )
        )

        assertEquals(
            Content(false, challenges),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `It fetches the partner challenges when selecting a region`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = Valid(
            de.cleema.android.core.models.User(
                name = "user",
                region = DRESDEN,
                isSupporter = false
            )
        )

        assertEquals(
            Content(false, listOf()),
            sut.uiState.value
        )

        val challenges = listOf(
            de.cleema.android.core.models.Challenge.of(title = "C1"),
            de.cleema.android.core.models.Challenge.of(title = "t2")
        )
        repository.stubChallenges(DRESDEN.id, challenges)

        sut.setRegion(UUID.randomUUID())

        assertEquals(
            Content(false, listOf()),
            sut.uiState.value
        )

        sut.setRegion(DRESDEN.id)

        assertEquals(
            Content(false, challenges),
            sut.uiState.value
        )

        collectJob.cancel()
    }
}
