/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.partnerchallenges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.models.*
import de.cleema.android.core.models.Challenge.Kind.Partner
import de.cleema.android.core.models.Challenge.Kind.Collective
import de.cleema.android.regions.RegionsList
import java.util.*


@Composable
fun PartnerChallenges(
    modifier: Modifier = Modifier,
    viewModel: PartnerChallengesViewModel = hiltViewModel(),
    onChallengeClick: (UUID) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PartnerChallenges(
        modifier,
        uiState,
        viewModel::setRegion,
        onChallengeClick
    )
}

@Composable
fun PartnerChallenges(
    modifier: Modifier = Modifier,
    state: PartnerChallengesUiState,
    onSelectRegion: (UUID) -> Unit = {},
    onChallengeClick: (UUID) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (state) {
            is PartnerChallengesUiState.Content -> {
                RegionsList(
                    onSelectRegion = { onSelectRegion(it.id) }
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.challenges) { challenge ->
                        val kind = challenge.kind
                        ChallengeCard(
                            title = challenge.title,
                            teaserText = challenge.teaserText,
                            image = challenge.image?.image,
                            startDate = challenge.startDate,
                            endDate = challenge.endDate,
                            partner = if (kind is Partner) kind.partner else if (kind is Collective) kind.partner else null,
                            modifier = Modifier
                                .fillParentMaxWidth()
                        ) { onChallengeClick(challenge.id) }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            is PartnerChallengesUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
        }
    }
}

class MinimumHeightState(minHeight: Dp? = null) {
    var minHeight by mutableStateOf(minHeight)
}

@Preview("Partner challenges", widthDp = 320)
@Composable
fun PartnerChallengesPreview() {
    PartnerChallenges(
        modifier = Modifier.fillMaxWidth(),
        state = PartnerChallengesUiState.Content(
            false, listOf(
                de.cleema.android.core.models.Challenge.of(
                    title = "Ameisen essen",
                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore",
                    kind = Partner(
                        Partner(
                            name = "Ameisenbär e.V."
                        )
                    )
                ),
                de.cleema.android.core.models.Challenge.of(
                    title = "Müll sammeln",
                    description = "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr",
                    kind = Partner(
                        Partner(
                            name = "Grüne Wiese GmbH"
                        )
                    )
                )
            )
        )
    )
}
