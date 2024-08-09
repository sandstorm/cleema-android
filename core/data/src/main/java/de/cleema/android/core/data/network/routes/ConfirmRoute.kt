package de.cleema.android.core.data.network.routes

import retrofit2.http.GET
import retrofit2.http.Query

internal interface ConfirmRoute {
    @GET("api/auth/email-confirmation")
    suspend fun confirmAccount(@Query("confirmation") code: String): Result<Unit>
}
