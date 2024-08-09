package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SocialGraphResponse(
    val followers: List<SocialGraphItemResponse>,
    val following: List<SocialGraphItemResponse>
)

@Serializable
data class SocialGraphItemResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val isRequest: Boolean,
    val user: SocialUserResponse
)

@Serializable
data class SocialUserResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val username: String,
    val avatar: ImageResponseBox?
)
