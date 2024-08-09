/*
 * Created by Kumpels and Friends on 2022-12-08
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.OfferResponse
import retrofit2.http.*
import java.util.*

internal interface MarketplaceRoute {
    @GET("/api/offers?populate=*&filters[\$or][1][isRegional][\$eq]=false")
    suspend fun getOffers(
        @Query("filters[\$or][0][region][uuid][\$eq]") regionID: UUID? = null
    ): Result<ApiResponse<List<OfferResponse>>>

    @GET("/api/offers/{id}")
    suspend fun getOffer(@Path("id") id: UUID): Result<ApiResponse<OfferResponse>>

    @PATCH("/api/offers/{id}/redeem")
    suspend fun redeemOffer(@Path("id") id: UUID): Result<ApiResponse<OfferResponse>>
}
