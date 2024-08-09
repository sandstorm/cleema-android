package de.cleema.android.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.App
import de.cleema.android.newuser.NewUserScreen


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        LoginUiState.AuthenticateUser -> UserAuthentication(modifier, dismissClick = viewModel::dismissLogin)
        is LoginUiState.CreateNewUser -> NewUserScreen(
            modifier,
            invitationCode = state.invitationCode,
            onExistingTapped = viewModel::switchToLogin
        )
        LoginUiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        is LoginUiState.LoggedIn -> App(modifier)
        is LoginUiState.PendingConfirmation -> PendingConfirmationScreen(
            state.credentials.mail,
            modifier,
            viewModel::reset
        )
    }
}
