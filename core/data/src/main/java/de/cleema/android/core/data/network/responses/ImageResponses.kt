/*
 * Created by Kumpels and Friends on 2022-12-06
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val name: String,
    val width: Int,
    val height: Int,
    val ext: String,
    val url: String
)

val ImageResponse.scale: Double
    get() {
        val filename = name.dropLast(ext.length)
        return when {
            filename.endsWith("@3x") -> 3.0
            filename.endsWith("@2x") -> 2.0
            else -> 1.0
        }
    }
