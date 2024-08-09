/*
 * Created by Kumpels and Friends on 2023-01-03
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Region(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val name: String
) {
    companion object {
        val LEIPZIG = Region(name = "Leipzig")
        val DRESDEN = Region(name = "Dresden")
        val PIRNA = Region(name = "Pirna")
    }
}
