/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

interface SponsorRepository {
    suspend fun getPackages(): Result<List<de.cleema.android.core.models.SponsorPackage>>
    suspend fun saveSponsorship(
        data: de.cleema.android.core.models.SponsorData,
        type: de.cleema.android.core.models.SponsorType
    ): Result<Unit>
}
