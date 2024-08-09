/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.AvatarRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeAvatarRepository : AvatarRepository {
    var stubbedAvatars: Channel<Result<List<de.cleema.android.core.models.IdentifiedImage>>> = Channel()

    override fun getAvatarsStream(): Flow<Result<List<de.cleema.android.core.models.IdentifiedImage>>> {
        return stubbedAvatars.receiveAsFlow()
    }
}
