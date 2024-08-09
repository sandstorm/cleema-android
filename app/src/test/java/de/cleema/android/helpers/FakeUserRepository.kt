/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.data.UserValue
import de.cleema.android.core.models.SocialGraph
import de.cleema.android.core.models.SocialGraphItem
import de.cleema.android.core.models.UserDetails
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*

class FakeUserRepository(
    var savedUser: UserValue? = null,
) : UserRepository {
    var deleteUserInvoked: Boolean? = null
    var logoutInvoked: Boolean? = null
    var updatedDetails: UserDetails? = null
    var unfollowedId: UUID? = null
    var stubbedFollowResult: Result<SocialGraphItem?> =
        Result.failure(RuntimeException(""))
    var followedInvitionCode: String? = null
    var onRegistrationInvitionCode: String? = null
    var stubbedUserValue: UserValue?
        get() {
            return userFlow.value
        }
        set(value) {
            userFlow.value = value
        }

    suspend fun stub(socialGraph: Result<SocialGraph>) {
        socialGraphChannel.send(socialGraph)
    }

    private var userFlow: MutableStateFlow<UserValue?> = MutableStateFlow(null)
    private var socialGraphChannel: Channel<Result<SocialGraph>> = Channel()

    override suspend fun save(value: UserValue) {
        savedUser = value
        userFlow.value = value
    }

    override fun getUserStream(): Flow<UserValue?> = userFlow

    override suspend fun logout() {
        logoutInvoked = true
    }

    override suspend fun deleteUser() {
        deleteUserInvoked = true
    }

    override suspend fun followInvitation(
        referralCode: String,
        onRegistration: Boolean
    ): Result<SocialGraphItem?> {
        if (onRegistration) {
            onRegistrationInvitionCode = referralCode
        } else {
            followedInvitionCode = referralCode
        }
        return stubbedFollowResult
    }

    override fun getSocialGraphStream() = socialGraphChannel.receiveAsFlow()

    override suspend fun unfollow(socialGraphItemId: UUID) {
        unfollowedId = socialGraphItemId
    }

    override suspend fun updateUser(details: UserDetails) {
        updatedDetails = details
    }

}
