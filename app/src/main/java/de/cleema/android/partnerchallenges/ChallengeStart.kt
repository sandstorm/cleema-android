package de.cleema.android.partnerchallenges

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.common.formatted
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.Light
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.FormatStyle

@Composable
fun ChallengeStart(date: Instant, modifier: Modifier = Modifier) {
    Surface(color = Light, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                stringResource(id = R.string.start_date_header),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                date.toLocalDateTime(TimeZone.UTC)
                    .formatted(FormatStyle.MEDIUM),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun ChallengeStartPreview() {
    CleemaTheme {
        ChallengeStart(
            date = Clock.System.now(), modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}
