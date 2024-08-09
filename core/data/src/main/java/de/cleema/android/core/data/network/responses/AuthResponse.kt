package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AuthResponse(val jwt: String? = null, val user: AuthUserResponse)

@Serializable
data class AuthUserResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val username: String
)
