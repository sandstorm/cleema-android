package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.requests.CreateUserRequest
import de.cleema.android.core.data.network.responses.AuthResponse
import retrofit2.http.*

internal interface AuthRoute {
    @POST("api/auth/local/register")
    suspend fun registerUser(@Body user: CreateUserRequest): Result<AuthResponse>

    @FormUrlEncoded
    @POST("api/auth/local")
    suspend fun login(
        @Field("identifier") user: String,
        @Field("password") password: String
    ): Result<AuthResponse>
}

