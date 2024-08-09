/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import de.cleema.android.becomesponsor.BecomeSponsorUiState.*
import de.cleema.android.becomesponsor.BecomeSponsorUiState.EnterData.ValidationError.IBAN_INVALID
import de.cleema.android.core.models.SponsorPackage.Companion.fan
import de.cleema.android.helpers.FakeIbanValidator
import de.cleema.android.helpers.FakeSponsorRepository
import de.cleema.android.helpers.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BecomeSponsorViewModelTest {
    private val COMPLETE = de.cleema.android.core.models.SponsorData(
        firstName = "Hans",
        lastName = "Bernd",
        postalCode = "12345",
        city = "city",
        streetAndHouseNumber = "street",
        iban = "123abasdfasdf"
    )
    private lateinit var validator: FakeIbanValidator
    private lateinit var repository: FakeSponsorRepository
    private lateinit var sut: BecomeSponsorViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeSponsorRepository()
        validator = FakeIbanValidator()
        sut = BecomeSponsorViewModel(repository, validator)
    }

    @Test
    fun `Loading from repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val packages = de.cleema.android.core.models.SponsorPackage.all.shuffled()
        repository.given(packages)
        assertEquals(
            SelectPackage(packages, packages.first()),
            sut.uiState.value
        )

        sut.onNextClick()

        assertEquals(EnterData(), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Postal codes are digits and has at most five characters`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        repository.given(de.cleema.android.core.models.SponsorPackage.all)

        sut.onNextClick()

        assertEquals(EnterData(), sut.uiState.value)

        sut.onEditSponsorData(de.cleema.android.core.models.SponsorData(postalCode = "123ab"))

        assertEquals(
            EnterData(de.cleema.android.core.models.SponsorData(postalCode = "123")), sut.uiState.value
        )

        sut.onEditSponsorData(de.cleema.android.core.models.SponsorData(postalCode = "12345"))

        assertEquals(
            EnterData(de.cleema.android.core.models.SponsorData(postalCode = "12345")), sut.uiState.value
        )

        sut.onEditSponsorData(de.cleema.android.core.models.SponsorData(postalCode = "123456"))

        assertEquals(
            EnterData(de.cleema.android.core.models.SponsorData(postalCode = "12345")), sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `IBAN validation`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        repository.given(de.cleema.android.core.models.SponsorPackage.all)
        sut.onNextClick()

        sut.onEditSponsorData(de.cleema.android.core.models.SponsorData(iban = "123abasdfasdf"))

        assertEquals(
            EnterData(
                de.cleema.android.core.models.SponsorData(iban = "123abasdfasdf"),
                IBAN_INVALID,
            ), sut.uiState.value
        )

        validator.stubbedValidation = true
        sut.onEditSponsorData(de.cleema.android.core.models.SponsorData(iban = "123abasdfasdfg"))

        assertEquals(
            EnterData(de.cleema.android.core.models.SponsorData(iban = "123abasdfasdfg")), sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Next button is enabled when all values are present and valid`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        repository.given(de.cleema.android.core.models.SponsorPackage.all)
        sut.onNextClick()
        validator.stubbedValidation = true
        val expected = de.cleema.android.core.models.SponsorData(
            firstName = "Hans",
            lastName = "Bernd",
            postalCode = "12345",
            city = "city",
            streetAndHouseNumber = "street",
            iban = "123abasdfasdf"
        )
        sut.onEditSponsorData(expected)

        assertEquals(
            EnterData(
                expected,
                nextButtonEnabled = true
            ), sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Next button in EnterData results in Confirm`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        repository.given(de.cleema.android.core.models.SponsorPackage.all)
        sut.onNextClick()
        validator.stubbedValidation = true
        val expected = de.cleema.android.core.models.SponsorData(
            firstName = "Hans",
            lastName = "Bernd",
            postalCode = "12345",
            city = "city",
            streetAndHouseNumber = "street",
            iban = "123abasdfasdf"
        )
        sut.onEditSponsorData(expected)

        sut.onNextClick()

        assertEquals(
            Confirm(
                fan,
                expected,
                false
            ), sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Selection of packages`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val packages = de.cleema.android.core.models.SponsorPackage.all
        repository.given(packages)
        assertEquals(
            SelectPackage(packages, packages.first()),
            sut.uiState.value
        )
        val expected = packages.random()
        sut.onSelectPackage(expected)

        assertEquals(SelectPackage(packages, expected), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Next button in Confirm will send the data and the package id to the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        repository.given(de.cleema.android.core.models.SponsorPackage.all)
        // select
        val selectedPackage = de.cleema.android.core.models.SponsorPackage.all.random()
        sut.onSelectPackage(selectedPackage)

        sut.onNextClick()
        validator.stubbedValidation = true
        sut.onEditSponsorData(COMPLETE)

        sut.onNextClick()

        assertEquals(
            Confirm(
                selectedPackage,
                COMPLETE,
                false
            ), sut.uiState.value
        )

        sut.onConfirmSepaClick()

        assertEquals(
            Confirm(
                selectedPackage,
                COMPLETE,
                true
            ), sut.uiState.value
        )

        sut.onNextClick()

        assertEquals(Thanks, sut.uiState.value)
        assertEquals(COMPLETE, repository.savedData)
        assertEquals(selectedPackage.type, repository.savedPackageId)

        collectJob.cancel()
    }

    @Test
    fun `Next button in Confirm when saving fails`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        repository.given(de.cleema.android.core.models.SponsorPackage.all)
        // select
        val selectedPackage = de.cleema.android.core.models.SponsorPackage.all.random()
        sut.onSelectPackage(selectedPackage)

        sut.onNextClick()
        validator.stubbedValidation = true
        sut.onEditSponsorData(COMPLETE)

        sut.onNextClick()

        sut.onConfirmSepaClick()

        repository.saveResult = Result.failure(RuntimeException("Test error"))
        sut.onNextClick()

        assertEquals(Error("Test error"), sut.uiState.value)
        assertEquals(COMPLETE, repository.savedData)
        assertEquals(selectedPackage.type, repository.savedPackageId)

        collectJob.cancel()
    }

    @Test
    fun `Selecting a package in confirm will navigate back to select package state`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val packages = de.cleema.android.core.models.SponsorPackage.all
        repository.given(packages)
        sut.onNextClick()
        validator.stubbedValidation = true
        sut.onEditSponsorData(COMPLETE)
        sut.onNextClick()

        assertEquals(Confirm(packages.first(), COMPLETE), sut.uiState.value)

        sut.onSelectPackage(packages.first())

        assertEquals(SelectPackage(packages, packages.first()), sut.uiState.value)

        sut.onNextClick()

        assertEquals(EnterData(COMPLETE, nextButtonEnabled = true), sut.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `Editing sponsor data in confirm will navigate back to enter data state`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val packages = de.cleema.android.core.models.SponsorPackage.all
        repository.given(packages)
        assertEquals(SelectPackage(packages, packages.first()), sut.uiState.value)
        sut.onNextClick()
        validator.stubbedValidation = true
        sut.onEditSponsorData(COMPLETE)
        assertEquals(EnterData(COMPLETE, nextButtonEnabled = true), sut.uiState.value)

        sut.onNextClick()
        assertEquals(Confirm(packages.first(), COMPLETE), sut.uiState.value)

        val expected = COMPLETE
        sut.onEditSponsorData(expected)

        assertEquals(EnterData(expected, null, true), sut.uiState.value)

        collectJob.cancel()
    }
}

