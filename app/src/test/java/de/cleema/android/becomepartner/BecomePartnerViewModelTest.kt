/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomepartner

import de.cleema.android.becomepartner.BecomePartnerUiState.Loading
import de.cleema.android.becomepartner.BecomePartnerUiState.SelectPackage
import de.cleema.android.di.MailOpener
import de.cleema.android.helpers.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class BecomePartnerViewModelTest {
    private lateinit var repository: FakePartnerRepository
    private lateinit var mailOpener: FakeMailOpener
    private lateinit var sut: BecomePartnerViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakePartnerRepository()
        mailOpener = FakeMailOpener()
        sut = BecomePartnerViewModel(repository, mailOpener)
    }

    @Test
    fun `Loading from repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val packages = de.cleema.android.core.models.PartnerPackage.all.shuffled()
        repository.given(packages)
        assertEquals(
            SelectPackage(packages, packages.first()),
            sut.uiState.value
        )

        collectJob.cancel()
    }

    @Test
    fun `Selection of packages`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val packages = de.cleema.android.core.models.PartnerPackage.all
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
    fun `Contact sends email`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val packages = de.cleema.android.core.models.PartnerPackage.all
        repository.given(packages)
        assertEquals(
            SelectPackage(packages, packages.first()),
            sut.uiState.value
        )
        val expected = packages.random()
        sut.onSelectPackage(expected)

        sut.onContactClick()

        assertEquals("partner@cleema.app", mailOpener.invokedRecipient)
        assertEquals("Partnerschaftsanfrage", mailOpener.invokedSubject)
        assertEquals("Gewähltes Paket: ${expected.title}\n\n", mailOpener.invokedBody)

        collectJob.cancel()
    }
}

class FakeMailOpener : MailOpener {
    var invokedRecipient: String? = null
    var invokedSubject: String? = null
    var invokedBody: String? = null

    override suspend fun openMail(recipient: String, subject: String, body: String) {
        invokedRecipient = recipient
        invokedSubject = subject
        invokedBody = body
    }
}

