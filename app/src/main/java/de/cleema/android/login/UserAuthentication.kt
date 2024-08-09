package de.cleema.android.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.components.ErrorHintView
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText


@Composable
fun UserAuthentication(
    modifier: Modifier = Modifier,
    viewModel: UserAuthenticationViewModel = hiltViewModel(),
    dismissClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    UserAuthentication(
        state = state,
        onNameChange = viewModel::enterName,
        onPasswordChange = viewModel::enterPassword,
        onLoginTapped = viewModel::login,
        onDismissClick = dismissClick,
        onClearClick = viewModel::onClearClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAuthentication(
    modifier: Modifier = Modifier,
    state: UserAuthenticationUiState,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginTapped: () -> Unit,
    onDismissClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier
            .background(Accent)
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (state) {
            is UserAuthenticationUiState.Credentials -> {
                val textFieldColors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White, focusedIndicatorColor = Action,
                    unfocusedIndicatorColor = Color.LightGray,
                )
                Box {
                    IconButton(
                        onClick = onDismissClick,
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = DefaultText)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.profile_close_button_description)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .height(63.dp)
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(DefaultText)
                    )
                }

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.authenticate_user_hint),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                )

                AnimatedVisibility(visible = state.errorMessage != null) {
                    ErrorHintView(text = state.errorMessage ?: "", onCloseClick = onClearClick)
                }

                val focusManager = LocalFocusManager.current
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.name_placeholder)) },
                    value = state.name,
                    onValueChange = onNameChange,
                    enabled = !state.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    colors = textFieldColors
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = state.password,
                    label = { Text(stringResource(R.string.password_placeholder)) },
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = onPasswordChange,
                    enabled = !state.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Previous)
                        },
                        onDone = {
                            onLoginTapped()
                        }
                    ),
                    colors = textFieldColors
                )

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        onClick = onLoginTapped,
                        colors = ButtonDefaults.buttonColors(containerColor = Action),
                        shape = MaterialTheme.shapes.medium,
                        enabled = state.canLogin,
                    ) {
                        Text(
                            text = stringResource(R.string.login_button_title),
                            color = Color.White
                        )
                    }

                    if (state.isLoading) {
                        CircularProgressIndicator()
                    }

                }
            }
            UserAuthenticationUiState.Success ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Accent)
                ) {
                    Text("Done")
                }

        }
    }
}

@Preview("Login", widthDp = 320)
@Composable
fun UserAuthenticationPreview() {
    var state by remember {
        mutableStateOf(UserAuthenticationUiState.Credentials("", password = "", false))
    }
    CleemaTheme {
        UserAuthentication(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            onNameChange = { state = state.copy(name = it) },
            onPasswordChange = { state = state.copy(password = it) },
            onLoginTapped = {},
            onDismissClick = {},
            onClearClick = {}
        )
    }
}
