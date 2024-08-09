/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.requests

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class IdRequest(@Serializable(with = UUIDSerializer::class) val uuid: UUID)

@Serializable
data class CreateUserRequest(
    var username: String,
    var password: String,
    var email: String,
    var acceptsSurveys: Boolean,
    var region: IdRequest,
    var avatar: IdRequest? = null,
    @Serializable(with = UUIDSerializer::class)
    var clientID: UUID? = null,
    var ref: String?
)
