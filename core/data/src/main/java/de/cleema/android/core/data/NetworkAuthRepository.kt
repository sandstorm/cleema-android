/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.AuthDataSource
import de.cleema.android.core.data.network.ConfirmDataSource
import de.cleema.android.core.data.network.UsersDataSource
import de.cleema.android.core.data.network.requests.CreateUserRequest
import de.cleema.android.core.data.network.requests.IdRequest
import de.cleema.android.core.data.network.responses.IdentifiedImageResponse
import de.cleema.android.core.data.network.responses.ImageResponseBox
import de.cleema.android.core.data.network.responses.UserResponse
import de.cleema.android.core.models.IdentifiedImage
import de.cleema.android.core.models.Size
import de.cleema.android.data.network.responses.ImageResponse
import de.cleema.android.data.network.responses.scale
import java.net.URL
import java.util.*
import javax.inject.Inject

class NetworkAuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val usersDataSource: UsersDataSource,
    private val confirmDataSource: ConfirmDataSource,
    private val baseURL: URL
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<de.cleema.android.core.models.User> =
        authDataSource.login(username, password).mapCatching { response ->
            authDataSource.clientId = response.user.uuid
            return usersDataSource.me().map {
                response.jwt?.let { jwt -> it.toUser(password, jwt, baseURL) }
                    ?: throw RuntimeException("Could not create user from response")
            }
        }

    override suspend fun register(
        username: String,
        password: String,
        email: String,
        acceptsSurveys: Boolean,
        regionId: UUID,
        avatarId: UUID?,
        localUserId: UUID?,
        referralCode: String?
    ) = authDataSource.register(
        CreateUserRequest(
            username = username,
            password = password,
            region = IdRequest(regionId),
            acceptsSurveys = acceptsSurveys,
            email = email,
            avatar = avatarId?.let { IdRequest(it) },
            clientID = localUserId,
            ref = referralCode
        )
    ).onSuccess { response ->
        authDataSource.clientId = response.user.uuid
    }.onFailure {
        // FIXME: Is this corrent?
        authDataSource.clientId = null
    }.map { Unit }

    override var clientId: UUID?
        get() = authDataSource.clientId
        set(value) {
            authDataSource.clientId = value
        }

    override suspend fun deauthorize() {
        authDataSource.logout()
    }

    override suspend fun confirmAccount(code: String): Result<Unit> =
        confirmDataSource.confirmAccount(code).map { }
}

fun UserResponse.toUser(password: String, token: String, baseURL: URL): de.cleema.android.core.models.User? {
    val region = region ?: return null
    return de.cleema.android.core.models.User(
        id = uuid,
        name = username,
        region = region.toRegion(),
        joinDate = createdAt,
        kind = de.cleema.android.core.models.User.Kind.Remote(password, email = email, token = token),
        followerCount = follows?.followers ?: 0,
        followingCount = follows?.following ?: 0,
        acceptsSurveys = acceptsSurveys,
        referralCode = referralCode ?: "",
        avatar = avatar?.toAvatar(baseURL),
        isSupporter = isSupporter ?: false
    )
}

fun ImageResponseBox.toImage(baseURL: URL) = image.toImage(baseURL)

fun ImageResponse.toImage(baseURL: URL) = de.cleema.android.core.models.RemoteImage.of(
    url = URL(baseURL, url).toString(),
    size = Size(width.toFloat() / scale.toFloat(), height.toFloat() / scale.toFloat())
)

fun IdentifiedImageResponse.toImage(baseURL: URL) = IdentifiedImage(
    uuid,
    image.toImage(baseURL)
)
