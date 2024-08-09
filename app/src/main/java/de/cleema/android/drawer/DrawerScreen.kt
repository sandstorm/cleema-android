/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.becomepartner.BecomePartnerScreen
import de.cleema.android.becomesponsor.BecomeSponsorScreen
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText

@Composable
fun DrawerScreen(
    modifier: Modifier = Modifier,
    viewModel: DrawerScreenViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .background(Accent)
            .fillMaxSize()
    ) {
        DrawerScreenContent(uiState, modifier, onDismiss)
    }
}

@Composable
fun DrawerScreenContent(
    state: DrawerScreenUiState,
    modifier: Modifier,
    omDismiss: () -> Unit
) {
    when (state) {
        is DrawerScreenUiState.Success -> {
            when (state.route) {
//                DrawerRoute.SPONSORSHIP -> BecomeSponsorScreen(
//                    modifier = Modifier.fillMaxWidth(),
//                    onCloseClick = omDismiss
//                )
//                DrawerRoute.PARTNERSHIP -> BecomePartnerScreen(
//                    modifier = Modifier.fillMaxWidth()
//                )
                else -> Column(
                    modifier.verticalScroll(
                        rememberScrollState()
                    ),
                ) {
                    de.cleema.android.core.components.MarkdownText(
                        state.content,
                        modifier
                            .padding(20.dp),
                        color = DefaultText
                    )
                }
            }
        }

        is DrawerScreenUiState.Error -> Text(state.reason, modifier)
        is DrawerScreenUiState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
