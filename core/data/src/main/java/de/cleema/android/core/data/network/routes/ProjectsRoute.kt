/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.ProjectResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

internal interface ProjectsRoute {
    @GET("api/projects?populate=*")
    suspend fun getProjects(
        @Query("filters[region][uuid][\$eq]") regionId: UUID? = null,
        @Query("joined") joined: Boolean? = null
    ): Result<ApiResponse<List<ProjectResponse>>>

    @GET("api/projects/{id}")
    suspend fun getProject(@Path("id") id: UUID): Result<ApiResponse<ProjectResponse>>

    @GET("api/projects?populate=*&isFaved=true")
    suspend fun getFavedProjects(): Result<ApiResponse<List<ProjectResponse>>>

    @PATCH("api/projects/{id}/fav")
    suspend fun favProject(@Path("id") id: UUID): Result<ApiResponse<ProjectResponse>>

    @PATCH("api/projects/{id}/unfav")
    suspend fun unfavProject(@Path("id") id: UUID): Result<ApiResponse<ProjectResponse>>

    @PATCH("api/projects/{id}/join")
    suspend fun joinProject(@Path("id") id: UUID): Result<ApiResponse<ProjectResponse>>

    @PATCH("api/projects/{id}/leave")
    suspend fun leaveProject(@Path("id") id: UUID): Result<ApiResponse<ProjectResponse>>
}
