package de.cleema.android.core.data

import de.cleema.android.core.models.User
import java.util.*

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(
        username: String,
        password: String,
        email: String,
        acceptsSurveys: Boolean,
        regionId: UUID,
        avatarId: UUID? = null,
        localUserId: UUID? = null,
        referralCode: String? = null
    ): Result<Unit>

    suspend fun deauthorize()
    suspend fun confirmAccount(code: String): Result<Unit>

    var clientId: UUID?
}

class AuthenticationException(override val message: String? = "Could not authenticate user") :
    Throwable(message = message)
