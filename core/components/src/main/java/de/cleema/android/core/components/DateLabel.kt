package de.cleema.android.core.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import de.cleema.android.core.common.formatted
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.FormatStyle

@Composable
fun DateLabel(
    @StringRes headingRes: Int,
    date: Instant,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(stringResource(headingRes), fontWeight = FontWeight.SemiBold)
        Text(
            date.toLocalDateTime(TimeZone.UTC)
                .formatted(FormatStyle.MEDIUM),
        )
    }
}
