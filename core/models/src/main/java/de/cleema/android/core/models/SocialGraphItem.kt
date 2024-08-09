package de.cleema.android.core.models

import java.util.*

data class SocialGraphItem(val id: UUID = UUID.randomUUID(), val isRequest: Boolean = false, val user: SocialUser)

data class SocialUser(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val avatar: RemoteImage? = null
)
