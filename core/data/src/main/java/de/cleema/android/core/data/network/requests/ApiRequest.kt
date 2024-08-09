package de.cleema.android.core.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class ApiRequest<ResponseType>(
    val data: ResponseType
)
