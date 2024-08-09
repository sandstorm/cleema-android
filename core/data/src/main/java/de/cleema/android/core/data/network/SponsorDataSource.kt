package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.requests.BecomeSponsorRequest

interface SponsorDataSource {
    suspend fun becomeSponsor(request: BecomeSponsorRequest): Result<Unit>
}
