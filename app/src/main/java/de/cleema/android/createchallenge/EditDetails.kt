/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createchallenge

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@Composable
fun EditDetails(
    interval: de.cleema.android.core.models.Challenge.Interval,
    start: LocalDate,
    end: LocalDate,
    isPublic: Boolean,
    onIntervalChanged: (de.cleema.android.core.models.Challenge.Interval) -> Unit,
    onStartChanged: (LocalDate) -> Unit,
    onEndChanged: (LocalDate) -> Unit,
    onPublicChanged: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
    canInviteFriends : Boolean = false,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        EditInterval(interval = interval, onIntervalChanged = onIntervalChanged)

        Column(verticalArrangement = Arrangement.spacedBy((-12).dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.challenge_start_label))
                Spacer(modifier.weight(1f))
                de.cleema.android.core.components.DatePicker(value = minOf(start, end), onValueChange = onStartChanged)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.challenge_end_label))
                Spacer(modifier.weight(1f))
                de.cleema.android.core.components.DatePicker(value = maxOf(start, end), onValueChange = onEndChanged)
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.challenge_is_public_label))
                Spacer(modifier.weight(1f))
                Switch(
                    checked = isPublic,
                    onCheckedChange = onPublicChanged,
                    enabled = canInviteFriends,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Action,
                        checkedBorderColor = Color.Transparent,
                        checkedTrackColor = Action.copy(alpha = 0.5f),
                        uncheckedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.White
                    )
                )
            }
            if (!canInviteFriends) {
                Text(
                    text = stringResource(R.string.create_challenge_invite_friends_tooltip),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            } else if (isPublic) {
                Text(
                    text = stringResource(R.string.create_challenge_public_legal_hint),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

    }
}

@Preview("Challenge details", widthDp = 320, showBackground = true)
@Composable
fun EditDetailsPreview() {
    data class Details(
        val isPublic: Boolean,
        val start: LocalDate,
        val end: LocalDate,
        val internal: de.cleema.android.core.models.Challenge.Interval
    )

    var value by remember {
        mutableStateOf(
            Details(
                false,
                Clock.System.now().toLocalDateTime(UTC).date,
                Clock.System.now().plus(30.days).toLocalDateTime(UTC).date,
                de.cleema.android.core.models.Challenge.Interval.values().random()
            )
        )
    }

    CleemaTheme {
        EditDetails(
            isPublic = value.isPublic,
            start = value.start,
            end = value.end,
            interval = value.internal,
            onIntervalChanged = {
                value = value.copy(internal = it)
            },
            onPublicChanged = {
                value = value.copy(isPublic = it)
            },
            onStartChanged = {
                value = value.copy(start = it)
            },
            onEndChanged = {
                value = value.copy(end = it)
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
