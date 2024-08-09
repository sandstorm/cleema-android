/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

class DefaultPartnerRepository : PartnerRepository {
    override suspend fun getPackages(): Result<List<de.cleema.android.core.models.PartnerPackage>> {
        return Result.success(de.cleema.android.core.models.PartnerPackage.all)
    }
}
