package de.cleema.android.core.data.network

interface ConfirmDataSource {
    suspend fun confirmAccount(code: String): Result<Unit>
}
