package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.DrawerContentResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface InfoRoute {
    @GET("api/{route}")
    suspend fun getContent(@Path("route") route: String): Result<ApiResponse<DrawerContentResponse>>
}
