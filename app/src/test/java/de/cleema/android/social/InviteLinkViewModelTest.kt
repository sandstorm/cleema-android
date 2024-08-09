/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.social

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.models.User.Kind.Remote
import de.cleema.android.helpers.FakeSocialClient
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.shared.CurrentUserUseCase
import de.cleema.android.social.InviteLinkUiState.Invite
import de.cleema.android.social.InviteLinkUiState.Loading
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
class InviteLinkViewModelTest {
    private lateinit var sut: InviteLinkViewModel
    private lateinit var client: FakeSocialClient
    private lateinit var repository: FakeUserRepository

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeUserRepository()

        client = FakeSocialClient()
        sut = InviteLinkViewModel(CurrentUserUseCase(repository), client)
    }

    @Test
    fun `It shares the users referral code on invite for remote users`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val user = de.cleema.android.core.models.User(
            name = "user",
            region = de.cleema.android.core.models.Region.LEIPZIG,
            kind = Remote("pw", "mail", "token"),
            referralCode = "1234",
            isSupporter = false
        )
        repository.stubbedUserValue = Valid(user)
        client.stubbedResult = Result.success(Unit)

        assertEquals(Invite(user.referralCode), sut.uiState.value)

        sut.inviteUsersClicked()

        assertEquals(user.referralCode, client.invokedCode)

        collectJob.cancel()
    }

    @Test
    fun `Local user share invitation links with empty referral code`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        val user = de.cleema.android.core.models.User(
            name = "user",
            region = de.cleema.android.core.models.Region.LEIPZIG,
            kind = de.cleema.android.core.models.User.Kind.Local,
            referralCode = "invalid for local users",
            isSupporter = false
        )
        repository.stubbedUserValue = Valid(user)
        client.stubbedResult = Result.success(Unit)

        assertEquals(Invite(""), sut.uiState.value)

        sut.inviteUsersClicked()

        assertEquals("", client.invokedCode)

        collectJob.cancel()
    }
}
