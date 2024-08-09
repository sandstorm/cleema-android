/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import kotlinx.datetime.Instant
import java.util.*

data class Trophy(
    val id: UUID,
    val date: Instant,
    val title: String,
    val image: RemoteImage
) {
    companion object {
        val empty = Trophy(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Instant.DISTANT_FUTURE,
            "",
            RemoteImage("")
        )
    }
}
