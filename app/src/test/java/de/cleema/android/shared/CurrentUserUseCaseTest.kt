/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.shared

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.FakeUserRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.*

class CurrentUserUseCaseTest {
    @Test
    fun `Fetching the region for the currently logged in user`() = runTest {
        val repo = FakeUserRepository()
        val expected = de.cleema.android.core.models.User(
            name = "user",
            region = de.cleema.android.core.models.Region(UUID.randomUUID(), "Leipzig"),
            isSupporter = false
        )
        repo.stubbedUserValue = Valid(expected)

        val sut = CurrentUserUseCase(repo)

        assertEquals(expected, sut())
    }
}
