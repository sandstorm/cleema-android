package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.requests.CreateUserRequest
import de.cleema.android.core.data.network.responses.AuthResponse
import java.util.*

interface AuthDataSource {
    suspend fun login(userName: String, password: String): Result<AuthResponse>
    suspend fun register(userRequest: CreateUserRequest): Result<AuthResponse>
    suspend fun logout()
    var clientId: UUID?
}
