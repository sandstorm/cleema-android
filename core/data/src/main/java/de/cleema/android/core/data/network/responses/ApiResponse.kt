package de.cleema.android.core.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<ResponseType>(
    val data: ResponseType?,
    val meta: Metadata?,
    val error: ErrorResponse?
)

@Serializable
data class Metadata(val pagination: Pagination?)

@Serializable
data class Pagination(
    val page: Int, val pageSize: Int, val pageCount: Int, val total: Int
)
