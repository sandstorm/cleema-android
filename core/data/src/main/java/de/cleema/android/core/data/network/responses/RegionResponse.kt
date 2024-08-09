package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.core.models.Region
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RegionResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val publishedAt: Instant?
) {
    fun toRegion() = Region(id = uuid, name = name)
}
