/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.data.network

import de.cleema.android.core.data.network.ConfirmDataSource
import de.cleema.android.core.data.network.routes.ConfirmRoute
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CleemaConfirmationDataSource @Inject constructor(val baseURL: URL, val json: Json) :
    ConfirmDataSource {
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addCallAdapterFactory(ResultCallAdapterFactory(json = json))
            .baseUrl(baseURL)
            .client(client)
            .build()
    }
    private val api: ConfirmRoute by lazy {
        retrofit.create(ConfirmRoute::class.java)
    }

    override suspend fun confirmAccount(code: String): Result<Unit> {
        return api.confirmAccount(code)
    }
}
