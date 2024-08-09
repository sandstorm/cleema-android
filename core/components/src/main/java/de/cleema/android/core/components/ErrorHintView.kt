/*
 * Created by Kumpels and Friends on 2023-01-31
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun ErrorHintView(text: String, modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = White,
        border = BorderStroke(2.dp, Action),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    localizedErrorMessage(text),
                    color = Action,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCloseClick) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Action)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.error_close_button),
                            tint = White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun localizedErrorMessage(errorMessage: String): String {
    return when (errorMessage) {
        "Username already taken" -> stringResource(R.string.registration_error_username_taken)
        "Email or Username are already taken" -> stringResource(R.string.registration_error_email_or_username_taken)
        "Invalid identifier or password" -> stringResource(R.string.registration_error_identifier_or_password_invalid)
        "R.string.name_validation" -> stringResource(id = R.string.name_validation)
        "R.string.region_validation" -> stringResource(id = R.string.region_validation)
        else -> errorMessage
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF59E5BF, widthDp = 450, heightDp = 200)
@Composable
fun ErrorHintViewPreview() {
    CleemaTheme() {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(20.dp)) {
            ErrorHintView(text = "This is an error message with a lot of text that goes over several lines!") {}
        }
    }
}
