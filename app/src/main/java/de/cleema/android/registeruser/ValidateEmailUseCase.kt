package de.cleema.android.registeruser

import java.text.Normalizer
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(text: String): String? {
        return if (text.isValidEmail()) text.trim() else null
    }
}

private val regex = Regex(
    "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

private fun String.isValidEmail() =
    this.isNotEmpty() && regex.matches(trim().removeNonSpacingMarks())

private fun String.removeNonSpacingMarks() =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")