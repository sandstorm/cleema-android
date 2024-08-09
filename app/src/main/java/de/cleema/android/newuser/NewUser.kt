/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.newuser

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import de.cleema.android.R
import de.cleema.android.core.components.ErrorHintView
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed
import de.cleema.android.createuser.*
import de.cleema.android.registeruser.*

private enum class Rows { LOCAL, REMOTE }

@Composable
private fun Rows.title(): String = when (this) {
    Rows.LOCAL -> stringResource(R.string.newuser_local)
    Rows.REMOTE -> stringResource(R.string.newuser_server)
}

@Composable
fun NewUserScreen(
    modifier: Modifier = Modifier,
    invitationCode: String? = null,
    createUserViewModel: CreateUserViewModel = hiltViewModel(),
    registerViewModel: RegisterUserViewModel = hiltViewModel(),
    onExistingTapped: () -> Unit
) {
    var state by remember { mutableStateOf(Rows.LOCAL) }
    val systemUiController = rememberSystemUiController()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = scrollState.value, key2 = invitationCode) {
        if (scrollState.isScrollInProgress) {
            focusManager.clearFocus()
        }
        invitationCode?.let {
            registerViewModel.setReferralCode(it)
        }
    }

    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
        systemUiController.setNavigationBarColor(Dimmed)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Accent)
            .systemBarsPadding()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp)
                .systemBarsPadding()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.newuser_welcome))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .height(63.dp),
                colorFilter = ColorFilter.tint(DefaultText)
            )

            Text(text = stringResource(R.string.newuser_hint), fontWeight = FontWeight.Bold)

            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(R.string.newuser_description),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            NewUser(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                selectedRow = state,
                createUserViewModel,
                registerViewModel
            ) {
                state = it
                createUserViewModel.onClearErrorMessage()
                registerViewModel.onClearErrorMessage()
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    onClick = {
                        when (state) {
                            Rows.LOCAL -> createUserViewModel.save()
                            Rows.REMOTE -> registerViewModel.save()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Action),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(id = R.string.save_button_title), color = Color.White)
                }

                TextButton(onClick = onExistingTapped) {
                    Text(text = stringResource(R.string.login_existing_account))
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(R.drawable.login_wave),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth()
            )
        }
        Box(
            modifier = modifier
                .align(Alignment.BottomStart)
                .background(Dimmed)
                .fillMaxWidth()
                .height(100.dp)
        ) {}
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NewUser(
    modifier: Modifier = Modifier,
    selectedRow: Rows,
    createUserViewModel: CreateUserViewModel = hiltViewModel(),
    registerViewModel: RegisterUserViewModel = hiltViewModel(),
    onChangeRow: (Rows) -> Unit = {}
) {
    val createState by createUserViewModel.uiState.collectAsStateWithLifecycle()
    val registerState by registerViewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth()) {
        val createUserUiState = createState
        val registerUserUiState = registerState

        val errorMessage = when {
            selectedRow == Rows.LOCAL && createUserUiState is CreateUserUiState.Edit -> {
                createUserUiState.errorMessage
            }
            selectedRow == Rows.REMOTE && registerUserUiState is RegisterUserUiState.Edit -> {
                registerUserUiState.errorMessage
            }
            else -> null
        }

        AnimatedVisibility(
            visible = errorMessage != null
        ) {
            ErrorHintView(
                text = errorMessage ?: "",
                modifier = Modifier
                    .padding(bottom = 8.dp),
                onCloseClick = {
                    if (selectedRow == Rows.LOCAL) {
                        createUserViewModel.onClearErrorMessage()
                    } else if (selectedRow == Rows.REMOTE) {
                        registerViewModel.onClearErrorMessage()
                    }
                }
            )
        }

        TabRow(
            selectedTabIndex = Rows.values().indexOf(selectedRow),
            modifier = Modifier.clip(
                MaterialTheme.shapes.medium.copy(
                    bottomStart = ZeroCornerSize,
                    bottomEnd = ZeroCornerSize
                )
            )
        ) {
            Rows.values().forEach { row ->
                Tab(
                    text = { Text(row.title()) },
                    onClick = { onChangeRow(row) },
                    selected = (row == selectedRow)
                )
            }
        }
        AnimatedContent(
            targetState = selectedRow,
            transitionSpec = { fadeIn() with fadeOut() }) { targetState ->
            when (targetState) {
                Rows.LOCAL -> CreateUser()
                Rows.REMOTE -> RegisterUser()
            }
        }
    }
}

@Preview("New user")
@Composable
fun NewUserPreview() {
    NewUser(modifier = Modifier.fillMaxSize(), Rows.LOCAL)
}
