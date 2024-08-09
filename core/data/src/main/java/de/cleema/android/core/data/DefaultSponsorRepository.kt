package de.cleema.android.core.data

import de.cleema.android.core.data.network.SponsorDataSource
import de.cleema.android.core.data.network.requests.BecomeSponsorRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class DefaultSponsorRepository(
    private val dataSource: SponsorDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : SponsorRepository {
    override suspend fun getPackages(): Result<List<de.cleema.android.core.models.SponsorPackage>> {
        return Result.success(de.cleema.android.core.models.SponsorPackage.all)
    }

    override suspend fun saveSponsorship(
        data: de.cleema.android.core.models.SponsorData,
        type: de.cleema.android.core.models.SponsorType
    ): Result<Unit> =
        withContext(ioDispatcher) {
            dataSource.becomeSponsor(data.toRequest(type))
        }
}

private fun de.cleema.android.core.models.SponsorData.toRequest(type: de.cleema.android.core.models.SponsorType): BecomeSponsorRequest =
    BecomeSponsorRequest(
        type.toType(),
        firstName,
        lastName,
        streetAndHouseNumber,
        postalCode,
        city,
        iban,
        bic.takeIf { it.isNotBlank() }
    )

private fun de.cleema.android.core.models.SponsorType.toType(): BecomeSponsorRequest.Type = when (this) {
    de.cleema.android.core.models.SponsorType.FAN -> BecomeSponsorRequest.Type.fan
    de.cleema.android.core.models.SponsorType.MAKER -> BecomeSponsorRequest.Type.maker
    de.cleema.android.core.models.SponsorType.LOVE -> BecomeSponsorRequest.Type.love
}
