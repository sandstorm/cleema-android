/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.models.Survey
import de.cleema.android.core.styling.CleemaTheme
import java.util.*
import kotlin.random.Random


@Composable
fun SurveysList(modifier: Modifier = Modifier, viewModel: SurveysViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SurveysList(
        modifier = modifier,
        state = uiState,
        onSurveyClicked = viewModel::onSurveyClicked
    )
}

@Composable
fun SurveysList(
    modifier: Modifier = Modifier,
    state: SurveyUiState,
    onSurveyClicked: (UUID) -> Unit,
) {
    when (state) {
        is SurveyUiState.Content -> SurveysList(
            modifier = modifier,
            surveys = state.surveys,
            onSurveyClicked = onSurveyClicked
        )
        is SurveyUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = state.reason)
        SurveyUiState.Loading -> Text("Loading ...")
        SurveyUiState.Denied -> return
    }
}

@Composable
fun SurveysList(
    modifier: Modifier = Modifier,
    surveys: List<Survey>,
    onSurveyClicked: (UUID) -> Unit
) {
    LazyRow(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(surveys) { survey ->
            SurveyCard(survey, Modifier.fillParentMaxWidth()) { onSurveyClicked(survey.id) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SurveysListPreview() {
    CleemaTheme() {
        SurveysList(surveys = (1..10).map {
            Survey(title = "Survey $it", description = LoremIpsum(Random.nextInt(1, 50)).values.joinToString(" "))
        }, onSurveyClicked = {})
    }
}

