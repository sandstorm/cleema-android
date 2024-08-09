package de.cleema.android.helpers

import de.cleema.android.core.data.AuthRepository
import de.cleema.android.core.data.AuthenticationException
import kotlinx.coroutines.channels.Channel
import java.util.*

class FakeAuthRepository : AuthRepository {
    data class Values(
        val username: String,
        val password: String,
        val email: String,
        val acceptsSurveys: Boolean,
        val regionId: UUID,
        val avatarId: UUID? = null,
        val localUserId: UUID? = null,
        val referralCode: String? = null,
    )

    var deauthorizeInvoked: Boolean? = null
    var error: AuthenticationException? = null
    var registeredValues: Values? = null
    var loggedInUser: Pair<String, String>? = null
    var stubbedUser: Channel<Result<de.cleema.android.core.models.User>> = Channel()
    var loggedInUsers: List<Pair<String, String>> = listOf()
    var stubbedRegistrationResult: Result<Unit> = Result.success(Unit)
    var confirmedCode: String? = null
    var confirmationResult = Result.success(Unit)

    override var clientId: UUID? = null

    override suspend fun login(username: String, password: String): Result<de.cleema.android.core.models.User> {
        error?.let {
            return Result.failure(it)
        }
        loggedInUser = Pair(username, password).also {
            loggedInUsers = loggedInUsers + it
        }
        return stubbedUser.receive()
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
    ): Result<Unit> {
        registeredValues =
            Values(username, password, email, acceptsSurveys, regionId, avatarId, localUserId, referralCode)
        return stubbedRegistrationResult
    }

    override suspend fun deauthorize() {
        deauthorizeInvoked = true
    }

    override suspend fun confirmAccount(code: String): Result<Unit> {
        confirmedCode = code
        return confirmationResult
    }
}
