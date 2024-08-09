package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.requests.ApiRequest
import de.cleema.android.core.data.network.requests.BecomeSponsorRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SponsorRoute {
    @POST("/api/support-membership")
    suspend fun becomeSponsor(@Body request: ApiRequest<BecomeSponsorRequest>): Result<Unit>
}
