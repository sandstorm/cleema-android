package de.cleema.android.core.data.network

import de.cleema.android.core.models.DrawerRoute

interface InfoDataSource {
    suspend fun getContent(route: DrawerRoute): Result<String>
}
