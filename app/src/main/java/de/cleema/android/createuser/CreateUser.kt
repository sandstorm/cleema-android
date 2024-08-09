/*
 * Created by Kumpels and Friends on 2023-01-28
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createuser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.regions.RegionsList
import de.cleema.android.regions.RegionsViewModel

@Composable
fun CreateUser(
    modifier: Modifier = Modifier,
    viewModel: CreateUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CreateUser(uiState, modifier, viewModel::update)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUser(
    state: CreateUserUiState,
    modifier: Modifier = Modifier,
    onUpdateUser: (EditUser) -> Unit,
    regionsViewModel: RegionsViewModel = hiltViewModel(),
) = when (state) {
    is CreateUserUiState.Edit -> Column(
        modifier = modifier
    ) {
        val textFieldColors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White, focusedIndicatorColor = Action,
            unfocusedIndicatorColor = Color.LightGray,
        )
        LaunchedEffect(key1 = state.user.region) {
            regionsViewModel.select(state.user.region?.id)
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = MaterialTheme.shapes.medium.copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)
        ) {
            TextField(
                value = state.user.name,
                onValueChange = { onUpdateUser(state.user.copy(name = it)) },
                label = { Text(stringResource(R.string.name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                colors = textFieldColors,
                shape = RectangleShape,
            )

            RegionsList(modifier = Modifier.fillMaxWidth(), regionsViewModel) {
                onUpdateUser(state.user.copy(region = it))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.user.acceptsSurveys,
                onCheckedChange = { onUpdateUser(state.user.copy(acceptsSurveys = it)) })
            Text(
                text = stringResource(id = R.string.approve_surveys_title),
                modifier = Modifier.clickable { onUpdateUser(state.user.copy(acceptsSurveys = !state.user.acceptsSurveys)) })
        }
    }
    is CreateUserUiState.Error -> Text(state.reason, modifier, color = Color.Red)
    is CreateUserUiState.Saved -> Text(stringResource(R.string.create_user_saved), modifier, color = Color.Black)
}

@Preview("Create user", widthDp = 320, heightDp = 500)
@Composable
fun CreateUserPreview() {
    CleemaTheme {
        CreateUser(
            modifier = Modifier
                .background(Accent)
                .padding(20.dp),
            state = CreateUserUiState.Edit(),
            onUpdateUser = {}
        )
    }
}
