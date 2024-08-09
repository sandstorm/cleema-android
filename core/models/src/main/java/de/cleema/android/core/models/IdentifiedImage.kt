/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@kotlinx.serialization.Serializable
data class IdentifiedImage(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val image: RemoteImage
)
