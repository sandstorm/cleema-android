/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createchallenge

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.models.EditChallenge
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallenge(
    state: EditChallenge,
    modifier: Modifier = Modifier,
    onChangeState: (EditChallenge) -> Unit = {},
    canInviteFriends: Boolean = false,
) {
    val colors = textFieldColors(
        containerColor = Color.White,
        focusedIndicatorColor = Action,
        unfocusedIndicatorColor = Color.LightGray
    )
    Column(
        modifier
            .fillMaxSize()
            .background(Accent)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(stringResource(R.string.challenge_template_hint), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.challenge_template_hint_saving), style = MaterialTheme.typography.bodyMedium)

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp),
            shadowElevation = 16.dp,
            shape = MaterialTheme.shapes.medium, color = Color.White
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                state.image?.image?.let {
                    de.cleema.android.core.components.RemoteImageView(
                        remoteImage = it, modifier = Modifier
                            .fillMaxWidth()
                            .height(225.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                TextField(
                    value = state.title,
                    onValueChange = { onChangeState(state.copy(title = it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1,
                    label = {
                        Text(
                            stringResource(id = R.string.challenge_title_section_header).uppercase(),
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    colors = colors
                )

                TextField(
                    value = state.teaserText, onValueChange = {
                        onChangeState(state.copy(teaserText = it))
                    }, modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(
                            stringResource(id = R.string.challenge_teaser_text_section_header).uppercase(),
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    colors = colors
                )

                TextField(
                    value = state.description, onValueChange = {
                        onChangeState(state.copy(description = it))
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    label = {
                        Text(
                            stringResource(id = R.string.challenge_description_section_header).uppercase(),
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    colors = colors
                )

                Section(R.string.challenge_details_section_header) {
                    EditDetails(
                        interval = state.interval,
                        start = state.start,
                        end = state.end,
                        isPublic = state.isPublic,
                        canInviteFriends = canInviteFriends,
                        onIntervalChanged = {
                            onChangeState(state.copy(interval = it))
                        },
                        onStartChanged = {
                            onChangeState(state.copy(start = it))
                        },
                        onEndChanged = {
                            onChangeState(state.copy(end = it))
                        },
                        onPublicChanged = {
                            onChangeState(state.copy(isPublic = it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Section(
    @StringRes headerRes: Int,
    modifier: Modifier = Modifier,
    headerSpacingDp: Int = 4,
    headerPaddingDp: Int = 16,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(headerSpacingDp.dp),
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(start = headerPaddingDp.dp),
            text = stringResource(headerRes).uppercase(),
            style = MaterialTheme.typography.titleSmall,
            color = DefaultText
        )
        content()
    }
}

@Preview("Create challege", widthDp = 400)
@Composable
fun CreateChallengePreview() {
    var state by remember {
        mutableStateOf(EditChallenge(regionId = UUID.randomUUID()))
    }
    CleemaTheme {
        CreateChallenge(
            state = state,
            onChangeState = {
                state = it
            }
        )
    }
}

