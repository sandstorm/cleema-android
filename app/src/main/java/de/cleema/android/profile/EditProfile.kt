/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import de.cleema.android.R
import de.cleema.android.core.components.ErrorHintView
import de.cleema.android.core.models.IdentifiedImage
import de.cleema.android.core.models.UserDetails
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed
import de.cleema.android.profile.ProfileUiState.Edit.Validation
import de.cleema.android.profile.ProfileUiState.Edit.Validation.*
import de.cleema.android.regions.RegionsList
import de.cleema.android.selectavatar.SelectAvatarList
import kotlin.random.Random

@Composable
fun EditProfileDetails(
    state: ProfileUiState.Edit,
    modifier: Modifier = Modifier,
    onEdit: (UserDetails) -> Unit,
    onClearErrorClick: () -> Unit
) {
    EditProfileDetails(
        state = state,
        modifier = modifier,
        onNameChange = { onEdit(state.details.copy(name = it)) },
        onRegionChange = { onEdit(state.details.copy(region = it)) },
        onPasswordChange = { onEdit(state.details.copy(password = it)) },
        onPasswordConfirmChange = { onEdit(state.details.copy(passwordConfirmation = it)) },
        onPreviousPasswordChange = { onEdit(state.details.copy(previousPassword = it)) },
        onAcceptSurveysChange = { onEdit(state.details.copy(acceptsSurveys = it)) },
        onEmailChange = { onEdit(state.details.copy(email = it)) },
        onAvatarChange = { onEdit(state.details.copy(avatar = it)) },
        onClearErrorClick = onClearErrorClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun EditProfileDetails(
    state: ProfileUiState.Edit,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit,
    onRegionChange: (de.cleema.android.core.models.Region) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onPreviousPasswordChange: (String) -> Unit,
    onAcceptSurveysChange: (Boolean) -> Unit,
    onEmailChange: (String) -> Unit,
    onAvatarChange: (IdentifiedImage) -> Unit,
    onClearErrorClick: () -> Unit
) {
    var showAvatars by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var showsPasswordsInPlainText by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        val textFieldColors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White, focusedIndicatorColor = Action,
            unfocusedIndicatorColor = Color.LightGray
        )

        AnimatedVisibility(visible = state.saveErrorMessage != null, modifier = Modifier.padding(bottom = 8.dp)) {
            ErrorHintView(text = state.saveErrorMessage ?: "", onCloseClick = onClearErrorClick)

            /*state.validation?.let {
                Text(
                    it.title(),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Action
                )
            }*/
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    state.details.avatar?.image?.let {
                        val imageSize = 104.dp
                        Box(
                            Modifier
                                .size(imageSize)
                        ) {
                            de.cleema.android.core.components.RemoteImageView(
                                remoteImage = it, contentScale = ContentScale.Crop, modifier = Modifier
                                    .size(imageSize)
                                    .clip(CircleShape)
                                    .clickable {
                                        showAvatars = !showAvatars
                                    }
                            )
                            val density = LocalDensity.current.density
                            val questionMarkSize = Size(13 * density, 21 * density)
                            ContextCompat.getDrawable(LocalContext.current, R.drawable.questionmark)
                                ?.toBitmap(
                                    questionMarkSize.width.toInt(),
                                    questionMarkSize.height.toInt()
                                )
                                ?.asImageBitmap()
                                ?.let { questionMark ->
                                    Canvas(
                                        modifier = Modifier.size(imageSize)
                                    ) {
                                        drawImage(
                                            image = questionMark,
                                            topLeft = Offset(
                                                x = (imageSize.toPx() - questionMarkSize.width) / 2f,
                                                y = (imageSize.toPx() - questionMarkSize.height) / 2f
                                            ),
                                            blendMode = BlendMode.Plus
                                        )
                                    }
                                }
                        }
                    } ?: Image(
                        painter = painterResource(id = R.drawable.avatar_placeholder),
                        contentDescription = stringResource(id = R.string.avatar_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(104.dp)
                            .border(BorderStroke(6.dp, DefaultText), CircleShape)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .clickable { showAvatars = true },
                        colorFilter = ColorFilter.tint(DefaultText)
                    )

                    Column {
                        TextField(
                            value = state.details.name,
                            label = { Text(stringResource(R.string.name_placeholder)) },
                            onValueChange = onNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors,
                            singleLine = true,
                            maxLines = 1
                        )

                        RegionsList(onSelectRegion = onRegionChange)
                    }
                }

                if (state.showsCredentials) {
                    TextField(
                        value = state.details.email,
                        label = { Text(stringResource(R.string.mail_placeholder)) },
                        onValueChange = onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email
                        ),
                    )

                    TextField(
                        value = state.details.password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.new_password_placeholder)) },
                        visualTransformation = if (showsPasswordsInPlainText) VisualTransformation.None else PasswordVisualTransformation(),
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
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        ),
                        colors = textFieldColors,
                        singleLine = true,
                        maxLines = 1
                    )

                    if (state.details.password.isNotEmpty()) {
                        TextField(
                            value = state.details.passwordConfirmation,
                            label = { Text(stringResource(R.string.confirm_new_password_placeholder)) },
                            visualTransformation = if (showsPasswordsInPlainText) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TogglePasswordVisibilityButton(showsPasswordsInPlainText) {
                                    showsPasswordsInPlainText = !showsPasswordsInPlainText
                                }
                            },
                            onValueChange = onPasswordConfirmChange,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Password
                            ),
                            colors = textFieldColors,
                            singleLine = true,
                            maxLines = 1
                        )

                        if (state.user.kind is de.cleema.android.core.models.User.Kind.Remote) {
                            Text(
                                stringResource(R.string.edit_profile_previous_password_hint),
                                modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Green,
                            )

                            TextField(
                                value = state.details.previousPassword,
                                label = { Text(stringResource(R.string.previous_password_placeholder)) },
                                visualTransformation = if (showsPasswordsInPlainText) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    TogglePasswordVisibilityButton(showsPasswordsInPlainText) {
                                        showsPasswordsInPlainText = !showsPasswordsInPlainText
                                    }
                                },
                                onValueChange = onPreviousPasswordChange,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Password
                                ),
                                colors = textFieldColors,
                                singleLine = true,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.details.acceptsSurveys,
                onCheckedChange = onAcceptSurveysChange
            )
            Text(
                text = stringResource(id = R.string.approve_surveys_title),
                modifier = Modifier.clickable { onAcceptSurveysChange(state.details.acceptsSurveys.not()) })
        }
    }

    if (showAvatars) {
        Dialog(
            onDismissRequest = { showAvatars = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
        ) {
            SelectAvatarList(
                selection = state.details.avatar?.id,
                onDismiss = { showAvatars = false },
                onSelectAvatar = onAvatarChange
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

@Composable
fun Validation.title(): String = when (this) {
    PASSWORD -> stringResource(R.string.edit_user_password_hint)
    PASSWORD_CONFIRMATION -> stringResource(R.string.edit_user_password_confirmation_hint)
    NAME -> stringResource(R.string.edit_user_name_hint)
    PREVIOUS_PASSWORD -> stringResource(R.string.edit_user_previous_password_hint)
    EMAIL_INVALID -> stringResource(R.string.edit_user_email_hint)
}

val ProfileUiState.Edit.showsCredentials: Boolean
    get() = when {
        user.kind is de.cleema.android.core.models.User.Kind.Remote || details.localUserId != null -> true
        else -> false
    }

@Preview("Edit profile", widthDp = 360)
@Composable
fun EditProfilePreview() {
    CleemaTheme {
        EditProfileDetails(
            ProfileUiState.Edit(
                de.cleema.android.core.models.UserDetails(
                    avatar = de.cleema.android.core.models.IdentifiedImage(
                        image = de.cleema.android.core.models.RemoteImage.of(
                            url = "https://loremflickr.com/320/320?random=${Random.nextInt(100, 10000)}",
                            de.cleema.android.core.models.Size(320f, 320f)
                        )
                    )
                ),
                de.cleema.android.core.models.User(name = "User", region = de.cleema.android.core.models.Region.PIRNA),
                PASSWORD_CONFIRMATION
            ),
            Modifier
                .fillMaxWidth()
                .background(Dimmed),
            {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}
