/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.models.Coordinates
import de.cleema.android.core.models.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val title: String,
    val coordinates: Coordinates
) {
    fun toLocation() =
        if ((-90.0..90.0).contains(coordinates.latitude) && (-180.0..180.0).contains(coordinates.longitude))
            Location(title, coordinates)
        else null
}
