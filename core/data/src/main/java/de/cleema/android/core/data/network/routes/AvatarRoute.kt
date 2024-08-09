/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.IdentifiedImageResponse
import retrofit2.http.GET

internal interface AvatarRoute {
    @GET("api/user-avatars?populate=*")
    suspend fun getAvatarList(): Result<ApiResponse<List<IdentifiedImageResponse>>>
}
