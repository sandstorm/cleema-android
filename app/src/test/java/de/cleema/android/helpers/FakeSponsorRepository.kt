/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.SponsorRepository
import kotlinx.coroutines.channels.Channel

class FakeSponsorRepository : SponsorRepository {
    var saveResult: Result<Unit> = Result.success(Unit)
    var savedData: de.cleema.android.core.models.SponsorData? = null
    var savedPackageId: de.cleema.android.core.models.SponsorType? = null
    val stubbedPackages = Channel<Result<List<de.cleema.android.core.models.SponsorPackage>>>()
    fun given(packages: List<de.cleema.android.core.models.SponsorPackage>) {
        stubbedPackages.trySend(Result.success(packages))
    }

    override suspend fun getPackages(): Result<List<de.cleema.android.core.models.SponsorPackage>> =
        stubbedPackages.receive()

    override suspend fun saveSponsorship(
        data: de.cleema.android.core.models.SponsorData,
        type: de.cleema.android.core.models.SponsorType
    ): Result<Unit> {
        savedData = data
        savedPackageId = type
        return saveResult
    }
}
