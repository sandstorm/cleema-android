/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.requests.UpdateUserRequest
import de.cleema.android.core.data.network.responses.*
import retrofit2.http.*
import java.util.*

internal interface UsersRoute {
    @GET("api/users/me")
    suspend fun getMe(): Result<ApiResponse<UserBox>>

    @FormUrlEncoded
    @POST("api/users/me/follows")
    suspend fun followInvitation(@Field("ref") referralCode: String): Result<ApiResponse<SocialGraphItemResponse>>

    @GET("/api/users/me/follows")
    suspend fun follows(): Result<ApiResponse<SocialGraphResponse>>

    @DELETE("/api/users/me/follows/{id}")
    suspend fun unfollow(@Path("id") socialGraphItemId: UUID): Result<ApiResponse<SocialGraphResponse>>

    @PATCH("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: UUID, @Body request: UpdateUserRequest): Result<UpdateUserResponse>

    @DELETE("/api/users/{id}")
    suspend fun delete(@Path("id") userId: UUID): Result<UpdateUserResponse>
}
