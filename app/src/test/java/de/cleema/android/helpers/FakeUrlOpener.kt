package de.cleema.android.helpers

import de.cleema.android.di.UrlOpener

class FakeUrlOpener : UrlOpener {
    var invokedUri: String? = null

    override suspend fun openUrl(uriString: String) {
        invokedUri = uriString
    }
}
