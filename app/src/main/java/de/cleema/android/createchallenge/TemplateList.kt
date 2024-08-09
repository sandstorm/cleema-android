/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createchallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.models.EditChallenge
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import java.util.*

@Composable
fun TemplateList(
    modifier: Modifier = Modifier,
    viewModel: ChallengeTemplatesViewModel = hiltViewModel(),
    onTemplateClick: (EditChallenge) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TemplateList(state = uiState, modifier = modifier, onTemplateClick = onTemplateClick)
}

@Composable
fun TemplateList(
    state: ChallengeTemplatesUiState,
    modifier: Modifier = Modifier,
    onTemplateClick: (EditChallenge) -> Unit
) {
    when (state) {
        is ChallengeTemplatesUiState.Content -> {
            Box(
                modifier
                    .fillMaxSize()
                    .background(Accent)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(state.templates) { template ->
                        TemplateItem(
                            title = template.title,
                            teaserText = template.teaserText,
                            logo = template.logo
                        ) {
                            onTemplateClick(template)
                        }
                    }
                }
            }
        }

        is ChallengeTemplatesUiState.Error -> de.cleema.android.core.components.ErrorScreen(
            modifier = modifier,
            message = state.reason
        )
        ChallengeTemplatesUiState.Loading -> de.cleema.android.core.components.LoadingScreen(modifier = modifier)
    }
}

@Preview("Challenge template list", widthDp = 320, heightDp = 800)
@Composable
fun TemplateListPreview() {
    CleemaTheme {
        TemplateList(
            state = ChallengeTemplatesUiState.Content(
                listOf(
                    EditChallenge(
                        title = "Ameisen kauen",
                        description = "Lorem ipsum dolor sit amet",
                        regionId = UUID.randomUUID()
                    ),
                    EditChallenge(
                        "Fahrrad fahren",
                        "sed diam nonumy eirmod tempor invidunt ut labore",
                        regionId = UUID.randomUUID()
                    ),
                    EditChallenge(
                        title = "Ameisen kauen",
                        description = "Lorem ipsum dolor sit amet",
                        regionId = UUID.randomUUID()
                    ),
                    EditChallenge(
                        "Fahrrad fahren",
                        "sed diam nonumy eirmod tempor invidunt ut labore",
                        regionId = UUID.randomUUID()
                    )
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {}
    }
}
