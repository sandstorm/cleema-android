/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

data class SponsorData(
    val firstName: String = "",
    val lastName: String = "",
    val streetAndHouseNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val iban: String = "",
    val bic: String = ""
) {
    companion object {
        val DEMO = SponsorData(
            firstName = "Hans-Bernd",
            lastName = "Cleema",
            streetAndHouseNumber = "Cleemastraße 1",
            postalCode = "51234",
            city = "Cleemadorf",
            iban = "DE91100000000123456789",
            bic = "MARKDEF1100"
        )
    }

    val isNotBlank: Boolean
        get() {
            return firstName.isNotBlank() &&
                    lastName.isNotBlank() &&
                    streetAndHouseNumber.isNotBlank() &&
                    city.isNotBlank() &&
                    postalCode.isNotBlank() &&
                    iban.isNotBlank()
        }
    val fullName: String
        get() = "$firstName $lastName"
    val address: String
        get() = "$streetAndHouseNumber\n$postalCode $city"
}
