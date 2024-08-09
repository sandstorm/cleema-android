package de.cleema.android.helpers

import de.cleema.android.core.data.MagazineRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FakeMagazineRepository : MagazineRepository {
    var invokedReadId: UUID? = null
    var invokedNewsRegion: UUID? = null
    val itemsChannel = Channel<Result<List<de.cleema.android.core.models.MagazineItem>>>()
    val itemChannel = Channel<Result<de.cleema.android.core.models.MagazineItem>>()
    var invokedItemId: UUID? = null

    override fun getMagazineItemsStream(regionId: UUID?): Flow<Result<List<de.cleema.android.core.models.MagazineItem>>> =
        flow {
            invokedNewsRegion = regionId
            emit(itemsChannel.receive())
        }

    override fun getMagazineItemStream(itemId: UUID): Flow<Result<de.cleema.android.core.models.MagazineItem>> = flow {
        invokedItemId = itemId
        emit(itemChannel.receive())
    }

    override fun getFavedMagazineItemsStream(): Flow<Result<List<de.cleema.android.core.models.MagazineItem>>> {
        TODO("Not yet implemented")
    }

    override suspend fun fav(itemId: UUID, faved: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun markAsRead(itemId: UUID) {
        invokedReadId = itemId
    }
}
