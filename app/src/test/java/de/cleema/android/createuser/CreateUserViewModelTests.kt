/*
 * Created by Kumpels and Friends on 2023-01-31
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createuser

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.createuser.CreateUserUiState.Saved
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserViewModelTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var repository: FakeUserRepository
    private val fixedId: UUID = UUID.fromString("deadbeef-dead-beef-dead-beafdeadbeef")
    private val fixedInstant: Instant = Instant.fromEpochMilliseconds(42)
    private lateinit var sut: CreateUserViewModel

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        sut = CreateUserViewModel(
            repository = repository,
            uuid = { fixedId },
            instant = { fixedInstant }
        )
    }

    private fun expectedFrom(edit: EditUser): de.cleema.android.core.models.User = de.cleema.android.core.models.User(
        id = fixedId,
        name = edit.name.trim(),
        region = edit.region!!,
        joinDate = fixedInstant,
        kind = de.cleema.android.core.models.User.Kind.Local,
        acceptsSurveys = edit.acceptsSurveys,
    )

    @Test
    fun `Editing users`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        var state = EditUser()
        assertEquals(
            CreateUserUiState.Edit(user = state, null),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            CreateUserUiState.Edit(user = state, "R.string.name_validation"),
            sut.uiState.value
        )

        state = state.copy(name = "Name")
        sut.update(state)

        sut.save()

        assertEquals(
            CreateUserUiState.Edit(user = EditUser(name = "Name"), "R.string.region_validation"),
            sut.uiState.value
        )

        state = state.copy(acceptsSurveys = true)
        sut.update(state)

        sut.save()

        assertEquals(
            CreateUserUiState.Edit(
                user = EditUser(name = "Name", acceptsSurveys = true),
                "R.string.region_validation"
            ),
            sut.uiState.value
        )

        state = state.copy(region = de.cleema.android.core.models.Region(id = fixedId, "Markkleeberg"))
        sut.update(state)

        assertEquals(
            CreateUserUiState.Edit(
                user = EditUser(
                    name = "Name",
                    region = de.cleema.android.core.models.Region(id = fixedId, "Markkleeberg"),
                    acceptsSurveys = true
                ), null
            ),
            sut.uiState.value
        )

        sut.save()

        collectJob.cancel()
    }

    @Test
    fun `Saving a user with invalid editState does nothing`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        sut.save()

        assertNull(repository.savedUser)
        collectJob.cancel()
    }

    @Test
    fun `Saving a valid user will trim `() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val edit = EditUser(
            "  User name    ",
            region = de.cleema.android.core.models.Region(id = fixedId, name = "Leipzig"),
            acceptsSurveys = true
        )
        sut.update(edit)

        sut.save()

        val expectedUser: de.cleema.android.core.models.User =
            de.cleema.android.core.models.User(
                id = fixedId,
                name = "User name",
                region = de.cleema.android.core.models.Region(id = fixedId, name = "Leipzig"),
                joinDate = fixedInstant,
                kind = de.cleema.android.core.models.User.Kind.Local,
                followerCount = 0,
                followingCount = 0,
                acceptsSurveys = true,
                isSupporter = false
            )
        assertEquals(Valid(expectedUser), repository.savedUser)

        assertEquals(Saved, sut.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun `Deleting a saved user will reset the state to edit`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        val edit = EditUser(
            "user",
            region = de.cleema.android.core.models.Region(id = fixedId, name = "Leipzig"),
            acceptsSurveys = true
        )
        sut.update(edit)
        sut.save()

        assertEquals(Valid(expectedFrom(edit)), repository.savedUser)
        assertEquals(Saved, sut.uiState.value)

        repository.stubbedUserValue = null

        assertEquals(
            CreateUserUiState.Edit(user = EditUser(), null),
            sut.uiState.value
        )

        collectJob.cancel()
    }

}
