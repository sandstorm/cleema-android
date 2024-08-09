package de.cleema.android.core.models

import de.cleema.android.core.models.Size.Companion.ZERO

@kotlinx.serialization.Serializable
data class Size(val width: Float, val height: Float) {
    companion object {
        val ZERO = Size(0f, 0f)
    }
}

@kotlinx.serialization.Serializable
data class RemoteImage(val url: String, val size: Size = ZERO) {

    companion object {
        fun of(url: String, size: Size = ZERO) = RemoteImage(url, size)
    }
}
