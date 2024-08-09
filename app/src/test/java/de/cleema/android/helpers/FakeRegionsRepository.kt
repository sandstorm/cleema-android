package de.cleema.android.helpers

import de.cleema.android.core.data.RegionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRegionsRepository : RegionsRepository {
    private var regionsStream =
        MutableStateFlow<Result<List<de.cleema.android.core.models.Region>>>(Result.success(listOf()))
    var regions: List<de.cleema.android.core.models.Region>
        get() = regionsStream.value.getOrElse {
            listOf()
        }
        set(value) {
            regionsStream.value = Result.success(value)
        }

    override fun getRegionsStream(): Flow<Result<List<de.cleema.android.core.models.Region>>> = regionsStream
}
