package de.cleema.android.social

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.models.User
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme


@Composable
fun InviteUsersByLink(
    modifier: Modifier = Modifier,
    viewModel: InviteLinkViewModel = hiltViewModel(),
    onDismissClicked: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state) {
        InviteLinkUiState.InvalidUserKind -> de.cleema.android.core.components.FailureAlert(
            stringResource(R.string.invite_users_denied_title),
            stringResource(R.string.invite_users_denied_body),
            stringResource(R.string.alert_dismiss_button_title),
            modifier,
            onDismissClicked
        )
        is InviteLinkUiState.Invite -> InviteUsersByLink(modifier = modifier, onShareClicked = {
            viewModel.inviteUsersClicked()
            onDismissClicked()
        }, onDismissClicked, viewModel.getUserKind() == User.Kind.Local)
        InviteLinkUiState.Loading -> de.cleema.android.core.components.LoadingScreen(modifier = modifier)
    }
}

@Composable
fun InviteUsersByLink(
    modifier: Modifier = Modifier,
    onShareClicked: () -> Unit,
    onDismissClicked: () -> Unit,
    userIsLocal: Boolean
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.invite_users_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    if (userIsLocal) stringResource(R.string.invite_users_body) else stringResource(R.string.invite_users_body_local),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onShareClicked,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Action
                    ),
                ) {
                    Text(stringResource(R.string.invite_users_share_link_button), color = Color.White)
                }
            }
            IconButton(
                onClick = onDismissClicked,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
            }
        }
    }
}

@Preview("Invite users", widthDp = 270)
@Composable
fun InviteUsersPreview() {
    CleemaTheme {
        InviteUsersByLink(modifier = Modifier.fillMaxWidth(), onShareClicked = {}, onDismissClicked = {}, true)
    }
}
