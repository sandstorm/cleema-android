/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

interface IBANValidator {
    fun isValid(iban: String): Boolean
}

class DefaultIBANValidator : IBANValidator {
    override fun isValid(iban: String): Boolean {
        if (!"^[\\dA-Z]*\$".toRegex().matches(iban)) {
            return false
        }
        val symbols = iban.trim { it <= ' ' }

        if (!(15..33).contains(symbols.length)) {
            return false
        }
        val swapped = symbols.substring(4) + symbols.substring(0, 4)
        return swapped.toCharArray()
            .map { it.code }
            .fold(0) { previousMod: Int, _char: Int ->
                val value = Integer.parseInt(_char.toChar().toString(), 36)
                val factor = if (value < 10) 10 else 100
                (factor * previousMod + value) % 97
            } == 1
    }
}
