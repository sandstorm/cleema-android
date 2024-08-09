/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.PartnerRepository
import kotlinx.coroutines.channels.Channel

class FakePartnerRepository : PartnerRepository {
    val stubbedPackages = Channel<Result<List<de.cleema.android.core.models.PartnerPackage>>>()
    fun given(packages: List<de.cleema.android.core.models.PartnerPackage>) {
        stubbedPackages.trySend(Result.success(packages))
    }

    override suspend fun getPackages(): Result<List<de.cleema.android.core.models.PartnerPackage>> =
        stubbedPackages.receive()
}
