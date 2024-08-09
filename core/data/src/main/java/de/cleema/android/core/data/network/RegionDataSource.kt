package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.RegionResponse
import java.util.*

interface RegionDataSource {
    suspend fun getRegions(regionId: UUID? = null): Result<List<RegionResponse>>
}
