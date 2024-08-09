package de.cleema.android.core.data.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    var status: Int,
    var name: String,
    var message: String,
    var details: ErrorDetails?
)

@Serializable
data class ErrorDetails(val reason: String?)

val ErrorResponse.description: String
    get() {
        var text = "name: $name, message: $message, status code: $status"
        details?.reason?.let {
            text += ", reason: $it"
        }
        return text
    }
