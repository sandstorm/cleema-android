/*
 * Created by Kumpels and Friends on 2022-11-09
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

data class Location(val title: String, val coordinates: Coordinates) {
    companion object {
        val LEIPZIG = Location("Leipzig", Coordinates(51.335486, 12.371892))
        val PIRNA = Location("Pirna", Coordinates(50.957778, 13.94))
        val DRESDEN = Location("Dresden", Coordinates(51.049259, 13.73836))
    }
}
