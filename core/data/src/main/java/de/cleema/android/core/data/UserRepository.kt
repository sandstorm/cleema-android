/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.models.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@kotlinx.serialization.Serializable
sealed interface UserValue {
    @kotlinx.serialization.Serializable
    data class Valid(val user: User) : UserValue

    @kotlinx.serialization.Serializable
    data class Pending(val credentials: Credentials) : UserValue
}

val UserValue.user: User?
    get() = when (this) {
        is UserValue.Pending -> null
        is UserValue.Valid -> this.user
    }

interface UserRepository {
    suspend fun save(value: UserValue)
    fun getUserStream(): Flow<UserValue?>
    fun getSocialGraphStream(): Flow<Result<SocialGraph>>
    suspend fun logout()
    suspend fun deleteUser()
    suspend fun followInvitation(referralCode: String, onRegistration: Boolean = false): Result<SocialGraphItem?>
    suspend fun unfollow(socialGraphItemId: UUID)
    suspend fun updateUser(details: UserDetails)
}
