package de.cleema.android.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun AccountConfirmation(mail: String, modifier: Modifier = Modifier, onResetTapped: () -> Unit) {
    Surface(modifier = modifier.padding(8.dp), shape = MaterialTheme.shapes.medium, color = Color.White) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(24.dp)) {
            de.cleema.android.core.components.MarkdownText(
                markdown = stringResource(
                    R.string.pending_user_validation_check_mails,
                    mail
                )
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onResetTapped,
                colors = ButtonDefaults.buttonColors(containerColor = Action),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = stringResource(R.string.pending_email_confirmation_reset),
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF00B091)
@Composable
fun AccountConfirmationPreview() {
    CleemaTheme() {
        AccountConfirmation(mail = "hi@there.com") {

        }
    }
}
