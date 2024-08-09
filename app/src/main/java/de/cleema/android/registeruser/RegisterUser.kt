/*
 * Created by Kumpels and Friends on 2023-01-28
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.registeruser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.components.LinkButton
import de.cleema.android.core.styling.Action
import de.cleema.android.createuser.CreateUserUiState.*
import de.cleema.android.regions.RegionsList
import de.cleema.android.registeruser.RegisterUserUiState.Edit.Validation.*


@Composable
fun RegisterUser(
    modifier: Modifier = Modifier,
    viewModel: RegisterUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is RegisterUserUiState.Edit -> {
            RegisterUser(
                modifier = modifier,
                state = state,
                onNameChanged = viewModel::enterName,
                onMailChanged = viewModel::enterMail,
                onPasswordChanged = viewModel::enterPassword,
                onConfirmationChanged = viewModel::enterConfirmation,
                toggleSurvey = viewModel::toggleAcceptsSurveys,
                selectRegion = viewModel::enterRegion
            )
        }

        RegisterUserUiState.Saved -> Text("Saved")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUser(
    modifier: Modifier = Modifier,
    state: RegisterUserUiState.Edit = RegisterUserUiState.Edit(),
    onNameChanged: (String) -> Unit,
    onMailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmationChanged: (String) -> Unit,
    toggleSurvey: () -> Unit,
    selectRegion: (de.cleema.android.core.models.Region) -> Unit,
) {
    var showsPasswordsInPlainText by remember { mutableStateOf(false) }

    Column(modifier) {
        val textFieldColors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White, focusedIndicatorColor = Action,
            unfocusedIndicatorColor = Color.LightGray,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = MaterialTheme.shapes.medium.copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)
        ) {
            TextField(
                value = state.name,
                label = { Text(stringResource(R.string.name_placeholder)) },
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                maxLines = 1,
                isError = false//state.errorMessage == NAME
            )
            TextField(
                value = state.email,
                label = { Text(stringResource(R.string.mail_placeholder)) },
                onValueChange = onMailChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                maxLines = 1,
                isError = false //state.errorMessageRes == MAIL.errorMessage()
            )

            TextField(
                value = state.password,
                label = { Text(stringResource(R.string.password_placeholder)) },
                visualTransformation = if (showsPasswordsInPlainText) None else PasswordVisualTransformation(),
                trailingIcon = {
                    Row {
                        de.cleema.android.core.components.InfoButton(modifier = Modifier.offset(y = 3.dp)) {
                            Text(
                                stringResource(R.string.form_password_hint),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(16.dp),
                            )
                        }

                        TogglePasswordVisibilityButton(showsPasswordsInPlainText) {
                            showsPasswordsInPlainText = !showsPasswordsInPlainText
                        }
                    }
                },
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true,
                maxLines = 1,
                isError = false //state.errorMessageRes == PASSWORD.errorMessage()
            )
            if (state.password.isNotEmpty()) {
                TextField(
                    value = state.confirmation,
                    label = { Text(stringResource(R.string.password_confirmation_placeholder)) },
                    visualTransformation = if (showsPasswordsInPlainText) None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TogglePasswordVisibilityButton(showsPasswordsInPlainText) {
                            showsPasswordsInPlainText = !showsPasswordsInPlainText
                        }
                    },
                    onValueChange = onConfirmationChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    singleLine = true,
                    maxLines = 1,
                    isError = false //state.errorMessageRes == PASSWORD_CONFIRMATION.errorMessage()
                )
            }

            RegionsList(onSelectRegion = selectRegion)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.acceptsSurveys,
                onCheckedChange = { toggleSurvey() })
            Text(
                text = stringResource(R.string.approve_surveys_title),
                modifier = Modifier.clickable(onClick = toggleSurvey)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LinkButton(
                title = stringResource(R.string.become_sponsor_privacy_link_title),
                uriString = stringResource(R.string.become_sponsor_privacy_url),
                style = MaterialTheme.typography.bodyLarge
            )

            LinkButton(
                title = stringResource(R.string.drawer_content_imprint),
                uriString = stringResource(R.string.imprint_url),
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }
}

@Composable
fun TogglePasswordVisibilityButton(showsPasswordsInPlainText: Boolean, onToggle: () -> Unit) {
    val image = if (showsPasswordsInPlainText)
        R.drawable.password_visibility
    else R.drawable.password_visibility_off
    // Please provide localized description for accessibility services
    val description =
        if (showsPasswordsInPlainText) stringResource(R.string.register_ax_hide_password) else stringResource(
            R.string.register_ax_show_password
        )

    IconButton(onClick = onToggle) {
        Icon(painter = painterResource(image), description, tint = Color.LightGray)
    }
}

@Preview(name = "Register user", widthDp = 320)
@Composable
fun RegisterUserPreview() {
    var state by remember {
        mutableStateOf(RegisterUserUiState.Edit())
    }
    RegisterUser(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onNameChanged = { state = state.copy(name = it) },
        onMailChanged = { state = state.copy(email = it) },
        onPasswordChanged = { state = state.copy(password = it) },
        onConfirmationChanged = { state = state.copy(confirmation = it) },
        toggleSurvey = { state = state.copy(acceptsSurveys = !state.acceptsSurveys) },
        selectRegion = { state = state.copy(region = it) }
    )
}
