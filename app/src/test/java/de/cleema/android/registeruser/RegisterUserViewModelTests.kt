/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.registeruser

import de.cleema.android.core.data.UserValue.Pending
import de.cleema.android.core.models.Credentials
import de.cleema.android.core.models.IdentifiedImage
import de.cleema.android.core.models.Region
import de.cleema.android.core.models.RemoteImage
import de.cleema.android.helpers.FakeAuthRepository
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.registeruser.RegisterUserUiState.Edit.Validation
import de.cleema.android.registeruser.RegisterUserUiState.Edit.Validation.*
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class TestFormatter : ValidationFormatter {
    var invokedValidation: Validation? = null
    override fun format(validation: Validation): String {
        invokedValidation = validation
        return validation.name
    }

    fun reset() {
        invokedValidation = null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterUserViewModelTests {
    private lateinit var formatter: TestFormatter

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    lateinit var repository: FakeAuthRepository
    lateinit var sut: RegisterUserViewModel
    private lateinit var userRepository: FakeUserRepository
    private val instant: Instant = Clock.System.now()
    private val userId: UUID = UUID.randomUUID()
    private val avatar =
        IdentifiedImage(image = RemoteImage.of("http://localhorstbern.de"))
    private val fakeUser =
        de.cleema.android.core.models.User(
            id = UUID.randomUUID(),
            name = "Fake user",
            region = Region(name = "Berlin"),
            joinDate = Instant.DISTANT_FUTURE,
            isSupporter = false,
            avatar = avatar
        )

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        userRepository = FakeUserRepository()
        formatter = TestFormatter()
        sut = RegisterUserViewModel(
            repository = repository,
            userRepository = userRepository,
            validateEmailUseCase = ValidateEmailUseCase(),
            uuid = { userId },
            instant = { instant },
            validationFormatter = formatter
        )
    }

    @Test
    fun `Entering user details will save the user`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        // name must have at least three characters
        for (i in 1..2) {
            val name = "Bernd".take(i)
            sut.enterName(name)

            assertEquals(
                RegisterUserUiState.Edit(name = name),
                sut.uiState.value
            )
            sut.save()

            assertEquals(
                RegisterUserUiState.Edit(name = name, errorMessage = NAME.name),
                sut.uiState.value
            )
        }

        sut.enterName("Bernd")

        assertEquals(
            RegisterUserUiState.Edit(name = "Bernd"),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(name = "Bernd", errorMessage = MAIL.name),
            sut.uiState.value
        )

        sut.enterMail("hi")

        assertEquals(
            RegisterUserUiState.Edit(name = "Bernd", email = "hi"),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(name = "Bernd", email = "hi", errorMessage = MAIL.name),
            sut.uiState.value
        )

        sut.enterMail("hi@there.com")

        assertEquals(
            RegisterUserUiState.Edit(name = "Bernd", email = "hi@there.com"),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(name = "Bernd", email = "hi@there.com", errorMessage = PASSWORD.name),
            sut.uiState.value
        )

        sut.enterPassword("12345")

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "12345"
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "12345",
                errorMessage = PASSWORD.name
            ),
            sut.uiState.value
        )

        sut.enterPassword("0123456789")

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789"
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                errorMessage = PASSWORD_CONFIRMATION.name
            ),
            sut.uiState.value
        )

        sut.enterConfirmation("12345")

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "12345"
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "12345",
                errorMessage = PASSWORD_CONFIRMATION.name
            ),
            sut.uiState.value
        )

        sut.enterConfirmation("0123456789")

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "0123456789",
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "0123456789",
                errorMessage = REGION.name
            ),
            sut.uiState.value
        )

        sut.toggleAcceptsSurveys()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "0123456789",
                acceptsSurveys = true,
                errorMessage = REGION.name
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "0123456789",
                acceptsSurveys = true,
                errorMessage = REGION.name
            ),
            sut.uiState.value
        )

        sut.toggleAcceptsSurveys()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "0123456789",
                acceptsSurveys = false,
                errorMessage = REGION.name
            ),
            sut.uiState.value
        )

        val region = Region(name = "Leipzig")
        sut.enterRegion(region)

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                confirmation = "0123456789",
                region = region,
                acceptsSurveys = false
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            FakeAuthRepository.Values(
                username = "Bernd",
                email = "hi@there.com",
                password = "0123456789",
                regionId = region.id,
                acceptsSurveys = false
            ),
            repository.registeredValues
        )
        val expected = Credentials("Bernd", "0123456789", "hi@there.com")
        assertEquals(Pending(expected), userRepository.savedUser)

        collectJob.cancel()
    }

    @Test
    fun `It does not save with invalid values`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        sut.save()

        assertNull(repository.registeredValues)
        assertNull(userRepository.savedUser)

        sut.enterRegion(Region(name = "Pirna"))
        sut.save()

        assertNull(repository.registeredValues)
        assertNull(userRepository.savedUser)

        sut.enterName("name")
        sut.save()

        assertNull(repository.registeredValues)
        assertNull(userRepository.savedUser)

        sut.enterMail("hi@there.com")
        sut.save()

        assertNull(repository.registeredValues)
        assertNull(userRepository.savedUser)

        sut.enterPassword("1111111111")
        sut.save()

        assertNull(repository.registeredValues)
        assertNull(userRepository.savedUser)

        sut.enterConfirmation("1111111111")
        sut.save()

        assertNotNull(repository.registeredValues)
        assertEquals(
            Pending(Credentials("name", "1111111111", "hi@there.com")),
            userRepository.savedUser
        )
        assertNull(userRepository.followedInvitionCode)
        assertNull(userRepository.onRegistrationInvitionCode)

        collectJob.cancel()
    }

    @Test
    fun `Saving with an error has the error message in the edit state`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        sut.enterName("Ronnybernd")
        sut.enterMail("hi@there.com")
        sut.enterPassword("geheimpolizei")
        sut.enterConfirmation("geheimpolizei")
        sut.enterRegion(de.cleema.android.core.models.Region.LEIPZIG)

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Ronnybernd",
                email = "hi@there.com",
                password = "geheimpolizei",
                confirmation = "geheimpolizei",
                region = de.cleema.android.core.models.Region.LEIPZIG,
                acceptsSurveys = false
            ),
            sut.uiState.value
        )
        repository.stubbedRegistrationResult = Result.failure(RuntimeException("Test error while user registration"))

        sut.save()

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Ronnybernd",
                email = "hi@there.com",
                password = "geheimpolizei",
                confirmation = "geheimpolizei",
                region = de.cleema.android.core.models.Region.LEIPZIG,
                acceptsSurveys = false,
                errorMessage = "Test error while user registration"
            ),
            sut.uiState.value
        )
        assertNull(userRepository.followedInvitionCode)
        assertNull(userRepository.onRegistrationInvitionCode)

        collectJob.cancel()
    }

    @Test
    fun `It sends the referral code when saving`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }

        sut.enterName("Ronnybernd")
        sut.enterMail("hi@there.com")
        sut.enterPassword("geheimpolizei")
        sut.enterConfirmation("geheimpolizei")
        sut.enterRegion(de.cleema.android.core.models.Region.LEIPZIG)
        sut.setReferralCode("abcd")

        assertEquals(
            RegisterUserUiState.Edit(
                name = "Ronnybernd",
                email = "hi@there.com",
                password = "geheimpolizei",
                confirmation = "geheimpolizei",
                region = de.cleema.android.core.models.Region.LEIPZIG,
                acceptsSurveys = false
            ),
            sut.uiState.value
        )

        sut.save()

        assertEquals(
            FakeAuthRepository.Values(
                username = "Ronnybernd",
                password = "geheimpolizei",
                email = "hi@there.com",
                acceptsSurveys = false,
                regionId = de.cleema.android.core.models.Region.LEIPZIG.id,
                referralCode = "abcd"
            ),
            repository.registeredValues
        )
        assertEquals("abcd", userRepository.onRegistrationInvitionCode)
        assertNull(userRepository.followedInvitionCode)

        collectJob.cancel()
    }
}

