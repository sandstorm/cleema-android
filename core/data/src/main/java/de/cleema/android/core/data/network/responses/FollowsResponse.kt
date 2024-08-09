package de.cleema.android.core.data.network.responses

@kotlinx.serialization.Serializable
data class FollowsResponse(
    val followers: Int,
    val followRequests: Int,
    val following: Int,
    val followingPending: Int
)
