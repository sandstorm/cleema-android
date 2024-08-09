/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.becomesponsor.IBANValidator

class FakeIbanValidator : IBANValidator {
    var stubbedValidation: Boolean = false
    var invokedIBAN: String? = null
    override fun isValid(iban: String): Boolean {
        invokedIBAN = iban
        return stubbedValidation
    }
}
