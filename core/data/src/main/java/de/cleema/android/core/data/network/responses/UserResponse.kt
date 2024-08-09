/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.data.network.responses.ImageResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserResponse(
    val username: String,
    val email: String,
    val createdAt: Instant,
    val referralCode: String?,
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val acceptsSurveys: Boolean,
    val region: RegionResponse?,
    val follows: FollowsResponse?,
    val avatar: IdentifiedImageResponse?,
    val isSupporter: Boolean?
)

@Serializable
data class ImageResponseBox(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val image: ImageResponse
)
