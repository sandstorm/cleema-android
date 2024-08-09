package de.cleema.android.core.data.network.requests

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BecomeSponsorRequest(
    val supportType: Type,
    val firstname: String,
    val lastname: String,
    val street: String,
    val zip: String,
    val city: String,
    val iban: String,
    val bic: String? = null
) {
    @kotlinx.serialization.Serializable
    enum class Type {
        @SerialName("fan")
        fan,

        @SerialName("macher")
        maker,

        @SerialName("liebe")
        love
    }
}
