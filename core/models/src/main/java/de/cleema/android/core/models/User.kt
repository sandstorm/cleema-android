/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import java.util.*

@Serializable
data class User @OptIn(ExperimentalSerializationApi::class) constructor(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val region: Region,
    val joinDate: Instant = Clock.System.now(),
    @EncodeDefault val kind: Kind = Kind.Local,
    @EncodeDefault val followerCount: Int = 0,
    @EncodeDefault val followingCount: Int = 0,
    @EncodeDefault val acceptsSurveys: Boolean = false,
    @EncodeDefault val referralCode: String = "",
    val avatar: IdentifiedImage? = null,
    @EncodeDefault val isSupporter: Boolean = false
) {
    @Serializable
    sealed interface Kind {
        @Serializable
        object Local : Kind

        @Serializable
        data class Remote(val password: String, val email: String, val token: String = "") : Kind
    }
}
