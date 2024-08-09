package de.cleema.android.partnerchallenges

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.cleema.android.R

@Composable
fun LeaveChallengeAlert(
    title: String,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        text = {
            Text(
                stringResource(id = R.string.leave_challenge_alert_message_format, title)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmClick
            ) {
                Text(stringResource(R.string.alert_leave_button_title))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick
            ) {
                Text(stringResource(R.string.alert_dismiss_button_title))
            }
        }
    )
}
