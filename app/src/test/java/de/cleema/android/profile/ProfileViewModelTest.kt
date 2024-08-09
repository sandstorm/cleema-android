/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import de.cleema.android.core.data.UserValue.Pending
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.*
import de.cleema.android.profile.ProfileUiState.Content
import de.cleema.android.profile.ProfileUiState.Edit
import junit.framework.TestCase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var details: de.cleema.android.core.models.UserDetails
    private lateinit var repository: FakeUserRepository
    private lateinit var sut: ProfileViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeUserRepository()
        authRepository = FakeAuthRepository()
        sut = ProfileViewModel(repository, authRepository)
        details = FAKE_REMOTE.toDetails()
    }

    private fun givenUser(user: de.cleema.android.core.models.User, shouldConvert: Boolean = false) {
        details = user.toDetails(shouldConvert)
        repository.stubbedUserValue = Valid(user)
    }

    @Test
    fun `Editing a remote user from the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(ProfileUiState.Loading, sut.uiState.value)
        givenUser(FAKE_REMOTE)

        assertEquals(Content(FAKE_REMOTE), sut.uiState.value)

        sut.startEditing()

        details = details.copy(name = "Hello")
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        details = details.copy(email = "foo@bar.de")
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        details = details.copy(region = PIRNA)
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        details = details.copy(password = "Geheim!!!")
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        details = details.copy(passwordConfirmation = "Geheim2!!!")
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        details = details.copy(previousPassword = "previous")
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        details = details.copy(acceptsSurveys = true)
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )
        val avatar = de.cleema.android.core.models.IdentifiedImage(
            image = de.cleema.android.core.models.RemoteImage.of(
                "fancyimageurl"
            )
        )
        details = details.copy(avatar = avatar)
        sut.edit(details)

        assertEquals(
            Edit(details, FAKE_REMOTE),
            sut.uiState.value
        )

        sut.cancelEditing()

        assertEquals(Content(FAKE_REMOTE), sut.uiState.value)
        assertNull(repository.savedUser)
        assertNull(repository.updatedDetails)
        collectJob.cancel()
    }

    @Test
    fun `Saving an edited user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(ProfileUiState.Loading, sut.uiState.value)
        givenUser(FAKE_REMOTE)
        sut.startEditing()

        details = details.copy(name = "Hello", password = "12345")
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_REMOTE, validation = Edit.Validation.PASSWORD),
            sut.uiState.value
        )

        details = details.copy(name = "Hello", password = "123456", passwordConfirmation = "08154711")
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_REMOTE, validation = Edit.Validation.PASSWORD_CONFIRMATION),
            sut.uiState.value
        )

        details = details.copy(name = "")
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_REMOTE, validation = Edit.Validation.NAME),
            sut.uiState.value
        )

        details = details.copy(
            name = "Bernd",
            email = "adfaasd",
            password = "123456",
            passwordConfirmation = "123456",
            previousPassword = FAKE_REMOTE.password!!
        )
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_REMOTE, validation = Edit.Validation.EMAIL_INVALID),
            sut.uiState.value
        )

        details = details.copy(
            name = "Heinz",
            password = "123456",
            email = "mail@post.de",
            passwordConfirmation = "123456",
            previousPassword = "flkgdfklgj"
        )
        sut.edit(details)

        sut.saveEditing()
        assertEquals(
            Edit(details, FAKE_REMOTE, validation = Edit.Validation.PREVIOUS_PASSWORD),
            sut.uiState.value
        )

        assertNull(repository.savedUser)
        assertNull(repository.updatedDetails)

        details = details.copy(
            name = "Heinz",
            password = "123456",
            passwordConfirmation = "123456",
            previousPassword = FAKE_REMOTE.password!!
        )
        sut.edit(details)

        sut.saveEditing()

        assertEquals(details, repository.updatedDetails)
        assertEquals(Content(FAKE_REMOTE), sut.uiState.value)
        assertNull(authRepository.registeredValues)

        collectJob.cancel()
    }

    @Test
    fun `Validating an edited user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(ProfileUiState.Loading, sut.uiState.value)
        givenUser(FAKE_REMOTE)
        sut.startEditing()

        details = details.copy(name = "Hello", password = "")
        sut.edit(details)
        sut.saveEditing()

        assertEquals(details, repository.updatedDetails)
        assertEquals(Content(FAKE_REMOTE), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Validating a local user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(ProfileUiState.Loading, sut.uiState.value)
        val expectedUser = FAKE_LOCAL.copy(
            avatar = de.cleema.android.core.models.IdentifiedImage(
                image = de.cleema.android.core.models.RemoteImage(
                    "http://localhorst.de"
                )
            )
        )
        givenUser(expectedUser)
        sut.startEditing()

        details = details.copy(name = "Hello", region = PIRNA)
        sut.edit(details)
        assertEquals(
            Edit(details, expectedUser, validation = null),
            sut.uiState.value
        )
        sut.saveEditing()

        assertEquals(details, repository.updatedDetails)
        assertEquals(Content(expectedUser), sut.uiState.value)
        assertNull(authRepository.registeredValues)

        collectJob.cancel()
    }

    @Test
    fun `Convert local user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUser(FAKE_LOCAL, true)
        sut.convert()

        assertEquals(
            Edit(details.copy(localUserId = FAKE_LOCAL.id), FAKE_LOCAL, validation = null),
            sut.uiState.value
        )
        details = details.copy(name = "Hello", password = "12345")
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_LOCAL, validation = Edit.Validation.PASSWORD),
            sut.uiState.value
        )
        assertNull(repository.savedUser)
        assertNull(repository.updatedDetails)

        details = details.copy(name = "Hello", password = "123456", passwordConfirmation = "08154711")
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_LOCAL, validation = Edit.Validation.PASSWORD_CONFIRMATION),
            sut.uiState.value
        )
        assertNull(repository.savedUser)
        assertNull(repository.updatedDetails)

        details = details.copy(name = "")
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_LOCAL, validation = Edit.Validation.NAME),
            sut.uiState.value
        )
        assertNull(repository.savedUser)
        assertNull(repository.updatedDetails)

        details = details.copy(
            name = "Bernd",
            email = "adfaasd",
            password = "123456",
            passwordConfirmation = "123456"
        )
        sut.edit(details)

        sut.saveEditing()

        assertEquals(
            Edit(details, FAKE_LOCAL, validation = Edit.Validation.EMAIL_INVALID),
            sut.uiState.value
        )
        assertNull(repository.savedUser)
        assertNull(repository.updatedDetails)

        details = details.copy(
            name = "Heinz",
            password = "123456",
            email = "mail@post.de",
            passwordConfirmation = "123456",
            previousPassword = "does not matter"
        )
        sut.edit(details)

        sut.saveEditing()
        authRepository.stubbedRegistrationResult = Result.success(Unit)

        assertNull(repository.updatedDetails)
        assertEquals(
            Pending(de.cleema.android.core.models.Credentials("Heinz", "123456", "mail@post.de")),
            repository.savedUser
        )
        assertEquals(Content(FAKE_LOCAL), sut.uiState.value)
        assertEquals(
            FakeAuthRepository.Values(
                details.name,
                details.password,
                details.email,
                details.acceptsSurveys,
                details.region!!.id,
                details.avatar?.id,
                FAKE_LOCAL.id,
            ), authRepository.registeredValues
        )
        assertNull(authRepository.clientId)

        collectJob.cancel()
    }

    @Test
    fun `Logout will deauthorize the auth view model`() = runTest {
        sut.logout()

        assertTrue(repository.logoutInvoked == true)
        assertTrue(authRepository.deauthorizeInvoked == true)
    }

    @Test
    fun `Deleting a local user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUser(FAKE_LOCAL)
        assertEquals(Content(FAKE_LOCAL, false), sut.uiState.value)

        sut.removeAccount()
        assertNull(repository.deleteUserInvoked)
        assertNull(authRepository.deauthorizeInvoked)

        assertEquals(Content(FAKE_LOCAL, showsRemoveAlert = true), sut.uiState.value)

        sut.cancelEditing()
        assertNull(repository.deleteUserInvoked)
        assertNull(authRepository.deauthorizeInvoked)

        sut.removeAccount()
        assertNull(repository.deleteUserInvoked)
        assertNull(authRepository.deauthorizeInvoked)

        assertEquals(Content(FAKE_LOCAL, showsRemoveAlert = true), sut.uiState.value)

        sut.saveEditing()

        assertEquals(Content(FAKE_LOCAL, showsRemoveAlert = false), sut.uiState.value)
        assertEquals(true, repository.deleteUserInvoked)
        assertEquals(true, authRepository.deauthorizeInvoked)

        collectJob.cancel()
    }

    @Test
    fun `Deleting a remote user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUser(FAKE_REMOTE)
        assertEquals(Content(FAKE_REMOTE, false), sut.uiState.value)

        sut.removeAccount()
        assertNull(repository.deleteUserInvoked)
        assertNull(authRepository.deauthorizeInvoked)

        assertEquals(Content(FAKE_REMOTE, showsRemoveAlert = true), sut.uiState.value)

        sut.cancelEditing()
        assertNull(repository.deleteUserInvoked)
        assertNull(authRepository.deauthorizeInvoked)

        sut.removeAccount()
        assertNull(repository.deleteUserInvoked)
        assertNull(authRepository.deauthorizeInvoked)

        assertEquals(Content(FAKE_REMOTE, showsRemoveAlert = true), sut.uiState.value)

        sut.saveEditing()

        assertEquals(Content(FAKE_REMOTE, showsRemoveAlert = false), sut.uiState.value)
        assertEquals(true, repository.deleteUserInvoked)
        assertEquals(true, authRepository.deauthorizeInvoked)

        collectJob.cancel()
    }

    @Test
    fun `Convert local user when the server answers with an error`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        givenUser(FAKE_LOCAL, true)
        sut.convert()

        details = details.copy(
            name = "Heinz",
            password = "123456",
            email = "mail@post.de",
            passwordConfirmation = "123456",
            previousPassword = "does not matter"
        )
        sut.edit(details)

        authRepository.stubbedRegistrationResult = Result.failure(RuntimeException("Test conversion error"))
        sut.saveEditing()


        assertNull(repository.updatedDetails)
        assertNull(repository.savedUser)
        assertEquals(
            Edit(details, FAKE_LOCAL, validation = null, saveErrorMessage = "Test conversion error"),
            sut.uiState.value
        )
        assertEquals(
            FakeAuthRepository.Values(
                details.name,
                details.password,
                details.email,
                details.acceptsSurveys,
                details.region!!.id,
                details.avatar?.id,
                FAKE_LOCAL.id,
            ), authRepository.registeredValues
        )
        assertNull(authRepository.clientId)

        collectJob.cancel()
    }
}
