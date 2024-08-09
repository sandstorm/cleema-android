/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.models.DrawerRoute

interface InfoRepository {
    suspend fun getContent(route: DrawerRoute): Result<String>
}
