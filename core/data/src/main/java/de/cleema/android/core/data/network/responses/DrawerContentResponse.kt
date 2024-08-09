/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */
package de.cleema.android.core.data.network.responses

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class DrawerContentResponse(
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val publishedAt: Instant?
)
