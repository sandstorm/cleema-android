/*
 * Created by Kumpels and Friends on 2022-12-19
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.TrophyItemResponse
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

internal interface TrophiesRoute {
    @GET("/api/trophies/me")
    suspend fun getTrophies(): Result<ApiResponse<List<TrophyItemResponse>>>

    @GET("/api/trophies/{id}")
    suspend fun getTrophy(@Path("id") trophyId: UUID): Result<ApiResponse<TrophyItemResponse>>
}
