package de.cleema.android.projects.details

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.cleema.android.R

@Composable
internal fun LeaveProjectAlert(
    projectTitle: String,
    onJoinClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        text = {
            Text(
                stringResource(id = R.string.leave_project_alert_message_format, projectTitle)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onJoinClick
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