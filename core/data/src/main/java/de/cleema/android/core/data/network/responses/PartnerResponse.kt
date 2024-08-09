package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.core.data.toImage
import de.cleema.android.core.models.Partner
import de.cleema.android.data.network.responses.ImageResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.net.URL
import java.util.*

@Serializable
data class PartnerResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val title: String,
    val url: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val publishedAt: Instant?,
    val description: String,
    val logo: ImageResponse?
)

fun PartnerResponse.toPartner(baseURL: URL): Partner =
    Partner(id = uuid, name = title, url = url, description = description, logo = logo?.toImage(baseURL))
