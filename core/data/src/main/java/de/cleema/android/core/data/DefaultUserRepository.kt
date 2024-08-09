/*
 * Created by Kumpels and Friends on 2023-01-05
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import de.cleema.android.core.common.BroadCaster
import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.core.data.network.UsersDataSource
import de.cleema.android.core.data.network.requests.IdRequest
import de.cleema.android.core.data.network.requests.UpdateUserRequest
import de.cleema.android.core.data.network.responses.SocialGraphResponse
import de.cleema.android.core.data.network.toGraph
import de.cleema.android.core.data.network.toItem
import de.cleema.android.core.models.SocialGraph
import de.cleema.android.core.models.SocialGraphItem
import de.cleema.android.core.models.User
import de.cleema.android.core.models.UserDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.*
import kotlin.Result.Companion.success

enum class PreferenceKeys {
    USER_KEY,
    VOUCHERS_KEY
}

class DefaultUserRepository(
    private val context: Context,
    private val usersDataSource: UsersDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val baseURL: URL,
) : UserRepository {
    private val socialGraphBroadCaster = BroadCaster<Result<SocialGraphResponse>>()
    private val sharedPreferences by lazy {
        val mainKey: MasterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "store",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    private var _userFlow: MutableStateFlow<UserValue?> =
        MutableStateFlow(
            kotlin.runCatching {
                sharedPreferences.getSerializable<UserValue>(PreferenceKeys.USER_KEY.name)
            }.getOrNull()
        )
    private var registeredInvitationReferralCode: String? = null

    override suspend fun save(value: UserValue) {
        with(sharedPreferences.edit()) {
            kotlin.runCatching {
                putSerializable(PreferenceKeys.USER_KEY.name, value)
            }.onSuccess {
                this.apply()
                _userFlow.value = value
            }
        }
    }

    override fun getUserStream(): Flow<UserValue?> = _userFlow

    override fun getSocialGraphStream(): Flow<Result<SocialGraph>> = flow {
        emit(usersDataSource.follows().map { it.toGraph(baseURL) })
        socialGraphBroadCaster.events.map { result ->
            result.map { it.toGraph(baseURL) }
        }.collect(this)
    }.flowOn(ioDispatcher)

    override suspend fun logout() {
        with(sharedPreferences.edit()) {
            remove(PreferenceKeys.USER_KEY.name)
            this.apply()
        }
        _userFlow.value = null
    }

    override suspend fun deleteUser() {
        _userFlow.value?.let { userValue ->
            when (userValue) {
                is Valid -> {
                    if (userValue.user.kind is User.Kind.Remote) {
                        // TODO: handle errors
                        usersDataSource.delete(userValue.user.id)
                    }
                }
                is UserValue.Pending -> Unit
            }
            with(sharedPreferences.edit()) {
                PreferenceKeys.values().forEach {
                    remove(it.name)
                }
                this.apply()
            }
            _userFlow.value = null
        }
    }

    override suspend fun followInvitation(referralCode: String, onRegistration: Boolean): Result<SocialGraphItem?> {
        if (onRegistration) {
            registeredInvitationReferralCode = referralCode
            return success(null)
        }
        return Result.runCatching {
            _userFlow.value?.user?.let { user ->
                val kind = user.kind
                if (kind is User.Kind.Remote) {
                    val socialGraphItem: SocialGraphItem? = if (registeredInvitationReferralCode != referralCode) {
                        usersDataSource.followInvitation(referralCode).getOrThrow().toItem(baseURL)
                    } else null
                    val me = usersDataSource.me().getOrThrow().toUser(kind.password, kind.token, baseURL)
                    _userFlow.value = me?.let(::Valid)
                    return success(socialGraphItem)
                } else null
            } ?: throw IllegalArgumentException("It is not possible to follow as a local user!")
        }
    }

    override suspend fun unfollow(socialGraphItemId: UUID) {
        socialGraphBroadCaster.post(usersDataSource.unfollow(socialGraphItemId))
        updateMe()
    }

    override suspend fun updateUser(details: UserDetails) {
        _userFlow.value?.user?.let { user ->
            when (user.kind) {
                is User.Kind.Remote -> {
                    // TODO: handle error
                    usersDataSource.updateUser(user.id, details.toRequest()).onSuccess {
                        updateMe()
                    }
                }

                User.Kind.Local -> {
                    save(
                        Valid(
                            user.copy(
                                name = details.name,
                                region = details.region ?: user.region,
                                acceptsSurveys = details.acceptsSurveys,
                                avatar = details.avatar
                            )
                        )
                    )
                }
            }
        }
    }

    private suspend fun updateMe() {
        _userFlow.value?.user?.let { user ->
            val kind = user.kind
            if (kind is User.Kind.Remote) {
                usersDataSource.me().onSuccess {
                    _userFlow.value = it.toUser(kind.password, kind.token, baseURL)?.let(::Valid)
                }
            }
        }
    }
}

private fun UserDetails.toRequest() = UpdateUserRequest(
    username = name.takeIf { it.isNotEmpty() },
    email = email.takeIf { it.isNotEmpty() },
    password = password.takeIf { it.isNotEmpty() },
    passwordRepeat = passwordConfirmation.takeIf { it.isNotEmpty() },
    acceptsSurveys = acceptsSurveys,
    avatar = avatar?.let { IdRequest(it.id) },
    region = region?.let { IdRequest(it.id) }
)

inline fun <reified S> SharedPreferences.getSerializable(key: String): S? {
    return getString(key, null)?.let {
        Json.decodeFromString(it) as? S
    }
}

inline fun <reified S> SharedPreferences.Editor.putSerializable(key: String, value: S) {
    val jsonString = Json.encodeToString(value)
    putString(key, jsonString).apply()
}
