package de.cleema.android.becomesponsor

import androidx.compose.foundation.shape.GenericShape

internal val SponsorRect = GenericShape { size, _ ->
    // 1)
    moveTo(0f, 0f)

    // 2)
    lineTo(size.width, 0f)

    lineTo(size.width - 30f, size.height / 2f)

    lineTo(size.width, size.height)
    // 3)
    lineTo(0f, size.height)

    lineTo(30f, size.height / 2f)

    lineTo(0f, 0f)
}
