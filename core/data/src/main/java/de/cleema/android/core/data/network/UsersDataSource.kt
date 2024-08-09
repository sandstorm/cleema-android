/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.requests.UpdateUserRequest
import de.cleema.android.core.data.network.responses.SocialGraphItemResponse
import de.cleema.android.core.data.network.responses.SocialGraphResponse
import de.cleema.android.core.data.network.responses.UpdateUserResponse
import de.cleema.android.core.data.network.responses.UserResponse
import java.util.*

interface UsersDataSource {
    suspend fun me(): Result<UserResponse>
    suspend fun followInvitation(referralCode: String): Result<SocialGraphItemResponse>
    suspend fun follows(): Result<SocialGraphResponse>
    suspend fun unfollow(socialGraphItemId: UUID): Result<SocialGraphResponse>
    suspend fun updateUser(id: UUID, request: UpdateUserRequest): Result<UpdateUserResponse>
    suspend fun delete(id: UUID): Result<UpdateUserResponse>
}
