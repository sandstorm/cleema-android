/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.shared

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class RegionForCurrentUserUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Fetching the region for the currently logged in user`() = runTest {
        for (region in listOf(LEIPZIG, DRESDEN, PIRNA)) {
            val repo = FakeUserRepository()
            val sut = RegionForCurrentUserUseCase(repo)

            repo.stubbedUserValue = Valid(
                de.cleema.android.core.models.User(
                    name = "user",
                    region = region,
                    isSupporter = false
                )
            )

            assertEquals(region, sut())
        }
    }
}
