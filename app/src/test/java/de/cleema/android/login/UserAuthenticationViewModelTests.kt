/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.login

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.FakeAuthRepository
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.login.UserAuthenticationUiState.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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
class UserAuthenticationViewModelTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var sut: UserAuthenticationViewModel
    private lateinit var userRepository: FakeUserRepository
    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        authRepository = FakeAuthRepository()
        sut = UserAuthenticationViewModel(userRepository, authRepository)
    }

    @Test
    fun `It logs a user in`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        sut.enterName("Name")

        assertEquals(Credentials("Name", isLoading = false), sut.uiState.value)

        sut.enterPassword("geheim")

        assertEquals(
            Credentials("Name", password = "geheim", false),
            sut.uiState.value
        )

        val expectedUser =
            de.cleema.android.core.models.User(
                id = UUID.randomUUID(),
                name = "User",
                region = de.cleema.android.core.models.Region(name = "Leipzig"),
                joinDate = Instant.DISTANT_FUTURE,
                isSupporter = false
            )

        sut.login()

        assertEquals(
            Credentials(name = "Name", password = "geheim", isLoading = true),
            sut.uiState.value
        )

        authRepository.stubbedUser.send(Result.success(expectedUser))

        assertEquals("Name", authRepository.loggedInUser?.first)
        assertEquals("geheim", authRepository.loggedInUser?.second)
        assertEquals(Valid(expectedUser), userRepository.savedUser)
        assertEquals(Success, sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `It has the error message when login fails`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        sut.enterName("Name")
        sut.enterPassword("geheim")

        assertEquals(Credentials("Name", password = "geheim", false), sut.uiState.value)


        sut.login()

        assertEquals(
            Credentials(name = "Name", password = "geheim", isLoading = true),
            sut.uiState.value
        )

        authRepository.stubbedUser.send(Result.failure(RuntimeException("Test login failed")))

        assertEquals("Name", authRepository.loggedInUser?.first)
        assertEquals("geheim", authRepository.loggedInUser?.second)
        assertNull(userRepository.savedUser)
        assertEquals(
            Credentials("Name", "geheim", false, "Test login failed"),
            sut.uiState.value
        )

        collectJob.cancel()
    }
}
