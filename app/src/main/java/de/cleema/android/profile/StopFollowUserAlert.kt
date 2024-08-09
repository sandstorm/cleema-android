/*
 * Created by Kumpels and Friends on 2023-01-03
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import de.cleema.android.R

@Composable
fun StopFollowUserAlert(
    userName: String,
    modifier: Modifier = Modifier,
    onRemoveClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissClick,
        text = {
            Text(
                stringResource(id = R.string.stop_following_user_alert_message, userName)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onRemoveClick
            ) {
                Text(stringResource(R.string.stop_following_user_button), color = Color.Red)
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
