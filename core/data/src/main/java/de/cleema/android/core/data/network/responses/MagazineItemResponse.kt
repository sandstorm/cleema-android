/*
 * Created by Kumpels and Friends on 2022-11-18
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.data.network.responses.ImageResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MagazineItemResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val title: String,
    val description: String,
    val teaser: String?,
    val date: Instant,
    val publishedAt: Instant,
    val tags: List<TagResponse>,
    val image: ImageResponse?,
    val type: Type?,
    val region: RegionResponse?,
    val isFaved: Boolean
) {
    @Serializable
    enum class Type { news, tip }
}
