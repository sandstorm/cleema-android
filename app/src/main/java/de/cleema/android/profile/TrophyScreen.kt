/*
 * Created by Kumpels and Friends on 2022-12-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.common.formatted
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import java.time.format.FormatStyle


@Composable
fun TrophyScreen(
    modifier: Modifier = Modifier,
    viewModel: TrophyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TrophyScreen(
        modifier = modifier,
        state = uiState,
        onClick = viewModel::startKonfetti,
        onEnd = viewModel::endKonfetti
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrophyScreen(
    modifier: Modifier = Modifier,
    state: TrophyUiState,
    onClick: () -> Unit,
    onEnd: () -> Unit,
) {
    Box(modifier) {
        when (state) {
            is TrophyUiState.Content -> Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            onClick = onClick,
                            modifier = Modifier
                                .size(208.dp),
                            color = Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                TrophyView(
                                    trophy = state.trophy,
                                    modifier = Modifier
                                        .size(104.dp)
                                        .scale(2f)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.trophy_received_on)
                            )
                            Text(text = state.trophy.date.toLocalDateTime(TimeZone.UTC).formatted(FormatStyle.LONG))
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    when (val newState = state.party) {
                        is TrophyViewModel.State.Started -> KonfettiView(
                            modifier = Modifier.fillMaxSize(),
                            parties = newState.party,
                            updateListener = object : OnParticleSystemUpdateListener {
                                override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                                    if (activeSystems == 0) onEnd()
                                }
                            }
                        )

                        TrophyViewModel.State.Idle -> {
                        }
                    }
                }
            }
            is TrophyUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
            TrophyUiState.Loading -> de.cleema.android.core.components.LoadingScreen()
        }
    }
}
