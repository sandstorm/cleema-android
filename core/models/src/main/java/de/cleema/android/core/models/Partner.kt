/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import java.util.*

data class Partner(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val url: String = "",
    val description: String = "",
    val logo: RemoteImage? = null
) {
    companion object {
        val DEMO = Partner(
            name = "Landeshauptstadt Dresden",
            url = "https://cleema.app/",
            description = "Das junge sächsische Startup hat eine Vision: Menschen zu nachhaltigem Handeln bewegen. Um unsere Utopie zu verwirklichen, starten wir in Dresden und Leipzig mit einer motivierenden App.",
            logo = RemoteImage.of("https://cleema.app/", Size(100f, 36f))
        )
    }

}
