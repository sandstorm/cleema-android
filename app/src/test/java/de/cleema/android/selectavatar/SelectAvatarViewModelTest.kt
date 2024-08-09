/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.selectavatar

import de.cleema.android.helpers.FakeAvatarRepository
import de.cleema.android.helpers.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class SelectAvatarViewModelTest {
    private lateinit var repository: FakeAvatarRepository
    private lateinit var sut: SelectAvatarViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeAvatarRepository()
        sut = SelectAvatarViewModel(repository)
    }

    @Test
    fun `It loads the avatars from the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(SelectAvatarUiState.Loading, sut.uiState.value)
        val avatars = listOf(
            de.cleema.android.core.models.IdentifiedImage(
                UUID.randomUUID(),
                de.cleema.android.core.models.RemoteImage.of(
                    "http://image1.de"
                )
            ),
            de.cleema.android.core.models.IdentifiedImage(
                UUID.randomUUID(),
                de.cleema.android.core.models.RemoteImage.of(
                    "http://image2.de"
                )
            )
        )
        repository.stubbedAvatars.trySend(Result.success(avatars))

        assertEquals(SelectAvatarUiState.Content(avatars), sut.uiState.value)

        repository.stubbedAvatars.trySend(Result.failure(RuntimeException("Test error loading avatars")))

        assertEquals(SelectAvatarUiState.Error("Test error loading avatars"), sut.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun `Selecting an avatar`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val avatars = listOf(
            de.cleema.android.core.models.IdentifiedImage(
                UUID.randomUUID(),
                de.cleema.android.core.models.RemoteImage.of("http://image1.de")
            ),
            de.cleema.android.core.models.IdentifiedImage(
                UUID.randomUUID(),
                de.cleema.android.core.models.RemoteImage.of("http://image2.de")
            )
        )
        repository.stubbedAvatars.trySend(Result.success(avatars))

        assertEquals(SelectAvatarUiState.Content(avatars), sut.uiState.value)

        sut.select(avatars[1].id)

        assertEquals(SelectAvatarUiState.Content(avatars, selection = avatars[1]), sut.uiState.value)

        sut.select(null)

        assertEquals(SelectAvatarUiState.Content(avatars, selection = null), sut.uiState.value)

        sut.select(avatars[0].id)

        assertEquals(SelectAvatarUiState.Content(avatars, selection = avatars[0]), sut.uiState.value)

        sut.select(UUID.randomUUID())

        assertEquals(SelectAvatarUiState.Content(avatars, selection = null), sut.uiState.value)

        collectJob.cancel()
    }
}

