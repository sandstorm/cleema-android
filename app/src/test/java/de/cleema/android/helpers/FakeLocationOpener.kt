package de.cleema.android.helpers

import de.cleema.android.core.models.Location
import de.cleema.android.di.LocationOpener

class FakeLocationOpener : LocationOpener {
    var openedLocation: de.cleema.android.core.models.Location? = null

    override suspend fun openLocation(location: de.cleema.android.core.models.Location) {
        openedLocation = location
    }
}
