package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.RegionResponse
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

internal interface RegionsRoute {
    @GET("api/regions")
    suspend fun getRegions(@Query("filters[uuid][\$eq]") id: UUID? = null): Result<ApiResponse<List<RegionResponse>>>
}
