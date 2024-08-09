/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.login

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.data.AuthenticationException
import de.cleema.android.core.data.UserValue.Pending
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.DeepLinking
import de.cleema.android.helpers.FakeAuthRepository
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.login.LoginUiState.*
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.Result.Companion.success

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: LoginViewModel
    private lateinit var userRepository: FakeUserRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var savedState: SavedStateHandle

    @Before
    fun setUp() {
        userRepository = FakeUserRepository()
        authRepository = FakeAuthRepository()
        savedState = SavedStateHandle()
        sut = LoginViewModel(userRepository, authRepository, savedState)
    }

    @Test
    fun `It will autologin a local user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        val user = de.cleema.android.core.models.User(
            id = UUID.randomUUID(),
            name = "user",
            region = de.cleema.android.core.models.Region(name = "Leipzig"),
            joinDate = Clock.System.now(),
            kind = de.cleema.android.core.models.User.Kind.Local,
            isSupporter = false
        )
        userRepository.stubbedUserValue = Valid(user)

        assertEquals(LoggedIn(user), sut.uiState.value)
        assertNull(authRepository.loggedInUser)
        assertEquals(user.id, authRepository.clientId)

        collectJob.cancel()
    }

    @Test
    fun `It will autologin an online user and save the logged in user to the user repository`() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
            val user = de.cleema.android.core.models.User(
                id = UUID.randomUUID(),
                name = "user",
                region = de.cleema.android.core.models.Region(name = "Leipzig"),
                joinDate = Clock.System.now(),
                kind = de.cleema.android.core.models.User.Kind.Remote(
                    password = "1234",
                    email = "mail@provider.de",
                    token = ""
                ),
                isSupporter = false
            )
            userRepository.stubbedUserValue = Valid(user)
            val expected = user.copy(
                kind = de.cleema.android.core.models.User.Kind.Remote(
                    password = "1234",
                    email = "mail@provider.de",
                    token = "server token"
                )
            )
            authRepository.stubbedUser.send(success(expected))

            assertEquals(LoggedIn(expected), sut.uiState.value)
            assertEquals("user", authRepository.loggedInUser?.first)
            assertEquals("1234", authRepository.loggedInUser?.second)
            assertEquals(Valid(expected), userRepository.savedUser)
            userRepository.stubbedUserValue = Valid(user.copy(name = "altered name"))
            assertEquals(listOf(Pair("user", "1234")), authRepository.loggedInUsers)

            collectJob.cancel()
        }

    @Test
    fun `It is in the login state when authentication fails`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        authRepository.error = AuthenticationException()
        val user = de.cleema.android.core.models.User(
            id = UUID.randomUUID(),
            name = "user",
            region = de.cleema.android.core.models.Region(name = "Leipzig"),
            joinDate = Clock.System.now(),
            kind = de.cleema.android.core.models.User.Kind.Remote(password = "1234", email = "mail@provider.de"),
            isSupporter = false
        )
        userRepository.stubbedUserValue = Valid(user)

        assertEquals(AuthenticateUser, sut.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun `It is in the create user state when no user is saved in the user repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = null

        assertEquals(CreateNewUser(), sut.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun `It will change to login state when login is tapped`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = null

        assertEquals(CreateNewUser(), sut.uiState.value)

        sut.switchToLogin()

        assertEquals(AuthenticateUser, sut.uiState.value)

        sut.dismissLogin()

        assertEquals(CreateNewUser(), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `A null user in the userRepository will deauthorize the auth repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = null

        assertTrue(authRepository.deauthorizeInvoked == true)
        collectJob.cancel()
    }

    @Test
    fun `It handles pending user validation`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        userRepository.stubbedUserValue = null
        assertEquals(CreateNewUser(), sut.uiState.value)

        val credentials = de.cleema.android.core.models.Credentials("user", "pw", "mail@mail.de")
        userRepository.stubbedUserValue = Pending(credentials)

        assertEquals(PendingConfirmation(credentials), sut.uiState.value)
        assertNull(authRepository.loggedInUser)
        assertNull(authRepository.clientId)

        sut.reset()

        assertEquals(true, userRepository.deleteUserInvoked)

        collectJob.cancel()
    }

    @Test
    fun `It logs the user in when confirm account in pending user validation`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val credentials = de.cleema.android.core.models.Credentials("user", "pw", "mail@mail.de")
        userRepository.stubbedUserValue = Pending(credentials)
        assertEquals(PendingConfirmation(credentials), sut.uiState.value)

        savedState.set(DeepLinking.confirmationCode, "1234")
        val user =
            de.cleema.android.core.models.User(name = "user", region = de.cleema.android.core.models.Region.LEIPZIG)
        authRepository.stubbedUser.trySend(success(user))

        assertEquals("1234", authRepository.confirmedCode)
        assertEquals(credentials.username to credentials.password, authRepository.loggedInUser)
        assertEquals(Valid(user), userRepository.savedUser)

        assertEquals(LoggedIn(user), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `It switches back to new user when account confirmation fails validation `() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val credentials = de.cleema.android.core.models.Credentials("user", "pw", "mail@mail.de")
        userRepository.stubbedUserValue = Pending(credentials)
        assertEquals(PendingConfirmation(credentials), sut.uiState.value)
        authRepository.confirmationResult = Result.failure(RuntimeException("test account validation error"))

        savedState.set(DeepLinking.confirmationCode, "1234")

        assertEquals("1234", authRepository.confirmedCode)
        assertNull(authRepository.loggedInUser)
        assertNull(userRepository.savedUser)

        assertEquals(CreateNewUser(), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `It sets the invitation code to the CreateNewUser ui state when no user is saved in the user repository`() =
        runTest {
            val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
            savedState.set(DeepLinking.invitationCode, "invitation code")
            userRepository.stubbedUserValue = null

            assertEquals(CreateNewUser("invitation code"), sut.uiState.value)

            collectJob.cancel()
        }
}
