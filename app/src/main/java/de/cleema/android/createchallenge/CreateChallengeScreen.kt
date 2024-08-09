package de.cleema.android.createchallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.createchallenge.CreateChallengeUiState.*
import de.cleema.android.inviteusers.InviteUsersScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateChallengeViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Accent,
                navigationIconContentColor = DefaultText,
                titleContentColor = DefaultText,
                actionIconContentColor = DefaultText
            ),
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.create_challenge_close_button)
                    )
                }
            },
            actions = {
                uiState.nextButtonTitle()?.let { title ->
                    TextButton(
                        onClick = viewModel::nextClicked,
                        enabled = uiState.canSave,
                    ) {
                        Text(title, color = DefaultText)
                    }
                }
            },
            title = {
                Text(stringResource(R.string.create_challenge), color = DefaultText)
            },
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        )
        when (val state = uiState) {
            is ChooseTemplate -> TemplateList(
                modifier = Modifier
                    .fillMaxSize(), onTemplateClick = viewModel::edit
            )
            is Edit -> CreateChallenge(state = state.editChallenge, onChangeState = viewModel::edit, canInviteFriends = state.canInviteFriends)
            is Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
            Done -> LaunchedEffect(key1 = state) {
                onClose()
            }
            is InviteUsers -> InviteUsersScreen(onSelectionChanged = viewModel::inviteUsersWithIds)
        }

    }
}

@Composable
fun CreateChallengeUiState.nextButtonTitle(): String? = when (this) {
    ChooseTemplate -> null
    Done -> null
    is Edit -> if (this.editChallenge.isPublic) stringResource(R.string.create_challenge_next_button) else stringResource(R.string.create_challenge_save)
    is Error -> null
    is InviteUsers -> stringResource(R.string.create_challenge_save)
}
