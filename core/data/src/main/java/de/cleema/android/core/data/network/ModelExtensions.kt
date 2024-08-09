package de.cleema.android.core.data.network

import de.cleema.android.core.data.*
import de.cleema.android.core.data.network.responses.SocialGraphItemResponse
import de.cleema.android.core.data.network.responses.SocialGraphResponse
import de.cleema.android.core.data.network.responses.SocialUserResponse
import java.net.URL

fun SocialGraphResponse.toGraph(baseURL: URL): de.cleema.android.core.models.SocialGraph =
    de.cleema.android.core.models.SocialGraph(
        followers = followers.map { it.toItem(baseURL) },
        following = following.map { it.toItem(baseURL) })

fun SocialGraphItemResponse.toItem(baseURL: URL) = de.cleema.android.core.models.SocialGraphItem(
    this.uuid, isRequest, this.user.toUser(baseURL)
)

fun SocialUserResponse.toUser(baseURL: URL) =
    de.cleema.android.core.models.SocialUser(uuid, username, avatar?.toImage(baseURL))
