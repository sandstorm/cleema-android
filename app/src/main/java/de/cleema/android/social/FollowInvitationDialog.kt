package de.cleema.android.social

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.components.FailureAlert
import de.cleema.android.core.components.LoadingScreen
import de.cleema.android.core.styling.CleemaTheme


@Composable
fun FollowInvitationDialog(
    modifier: Modifier = Modifier,
    viewModel: FollowInvitationViewModel = hiltViewModel(),
    onDismissClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is FollowInvitationUiState.Error -> FailureAlert(
            title = stringResource(R.string.invite_users_denied_title),
            stringResource(R.string.invite_users_error_body_format, state.reason),
            dismissButtonTitle = stringResource(R.string.invite_users_error_dismiss_title),
            modifier = modifier.wrapContentHeight(),
            onDismissClicked = onDismissClicked
        )
        is FollowInvitationUiState.Following -> FollowInvitationDialog(
            modifier = modifier,
            newFriend = state.item.user.username,
            onDismissClicked = onDismissClicked
        )
        FollowInvitationUiState.FollowedOnRegistration ->
            SideEffect {
                onDismissClicked()
            }
        FollowInvitationUiState.Loading -> LoadingScreen(modifier)
    }
}

@Composable
internal fun FollowInvitationDialog(
    newFriend: String,
    modifier: Modifier = Modifier,
    onDismissClicked: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.follow_new_user_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.follow_new_user_body, newFriend),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            TextButton(onClick = onDismissClicked) {
                Text(stringResource(R.string.new_user_close))
            }
        }
    }
}

@Preview("Followed an invitation", widthDp = 280)
@Composable
fun FollowInvitationDialogPreview() {
    CleemaTheme {
        FollowInvitationDialog(newFriend = "Kumpel", modifier = Modifier.fillMaxWidth()) {}
    }
}
