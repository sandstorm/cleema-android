package de.cleema.android.helpers

import de.cleema.android.social.SocialClient

class FakeSocialClient : SocialClient {
    var stubbedResult: Result<Unit> = Result.success(Unit)
    var invokedCode: String? = null
    override suspend fun invite(referralCode: String): Result<Unit> {
        invokedCode = referralCode
        return stubbedResult
    }
}
