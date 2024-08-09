/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Recessed
import de.cleema.android.profile.TrophiesUiState.*
import java.util.*

@Composable
fun TrophiesView(
    state: TrophiesUiState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTrophyClick: (UUID) -> Unit = {}
) {
    when (state) {
        is Content -> TrophiesContent(state.trophies, modifier, enabled, onTrophyClick)
        is Error -> de.cleema.android.core.components.ErrorScreen(
            message = state.reason,
            modifier = modifier.padding(horizontal = 20.dp)
        )
        Loading -> Box(
            modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun TrophiesContent(
    trophies: List<de.cleema.android.core.models.Trophy>,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onTrophyClicked: (UUID) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(id = R.string.profile_trophies_title),
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) Accent else Accent.copy(alpha = 0.4f)
        )

        if (trophies.isEmpty()) {
            Text(
                stringResource(id = R.string.trophies_empty_label),
                color = if (enabled) Recessed else Recessed.copy(alpha = 0.4f),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        } else {
            FlowRow(
                modifier = modifier.fillMaxWidth(),
                // FIXME: this should not be hardcoded
                mainAxisAlignment = if (trophies.count() >= 3) MainAxisAlignment.SpaceBetween else MainAxisAlignment.Start,
                mainAxisSize = SizeMode.Expand,
                mainAxisSpacing = 16.dp,
                crossAxisSpacing = 24.dp
            ) {
                for (trophy in trophies) {
                    TrophyView(trophy,
                        modifier = Modifier
                            .size(104.dp)
                            .alpha(if (enabled) 1f else 0.35f)
                            .clickable(
                                enabled = enabled,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = Color.White),
                                onClick = {
                                    onTrophyClicked(trophy.id)
                                }
                            )
                    )
                }
            }
        }
    }
}

