package de.cleema.android.core.models

@kotlinx.serialization.Serializable
data class Credentials(val username: String, val password: String, val mail: String)
