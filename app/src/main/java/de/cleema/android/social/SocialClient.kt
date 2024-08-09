package de.cleema.android.social

interface SocialClient {
    suspend fun invite(referralCode: String): Result<Unit>
}
