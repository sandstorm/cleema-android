/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.MagazineItemResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*


internal interface MagazineRoute {
    @GET("api/news-entries?populate=*&filters[\$or][2][type][\$eq]=tip")
    suspend fun getMagazineItems(
        @Query("filters[\$or][1][region][uuid][\$eq]")
        regionId: UUID?
    ): Result<ApiResponse<List<MagazineItemResponse>>>

    @GET("api/news-entries?populate=*&isFaved=true")
    suspend fun getFavedMagazineItems(): Result<ApiResponse<List<MagazineItemResponse>>>

    @GET("api/news-entries/{id}?populate=*")
    suspend fun getMagazineItem(@Path("id") id: UUID): Result<ApiResponse<MagazineItemResponse>>

    @PATCH("api/news-entries/{id}/fav")
    suspend fun favMagazineItem(@Path("id") id: UUID): Result<ApiResponse<MagazineItemResponse>>

    @PATCH("api/news-entries/{id}/unfav")
    suspend fun unfavMagazineItem(@Path("id") id: UUID): Result<ApiResponse<MagazineItemResponse>>

    @PATCH("/api/news-entries/{id}/read")
    suspend fun markMagazineItemAsRead(@Path("id") id: UUID): Result<ApiResponse<MagazineItemResponse>>
}
