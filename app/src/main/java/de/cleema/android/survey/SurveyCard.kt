/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.survey

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.models.SurveyState.Evaluation
import de.cleema.android.core.models.SurveyState.Participation
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import java.util.*

@Composable
fun SurveyCard(
    survey: de.cleema.android.core.models.Survey,
    modifier: Modifier = Modifier,
    onSurveyClicked: (de.cleema.android.core.models.Survey) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .shadow(19.dp, RoundedCornerShape(10.dp)),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.wave_long),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(survey.cardTitle(), style = MaterialTheme.typography.titleSmall)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(survey.title, style = MaterialTheme.typography.bodyMedium)
                    Text(survey.description, style = MaterialTheme.typography.bodyMedium)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { onSurveyClicked(survey) },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Action
                        ),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(text = survey.buttonTitle(), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun de.cleema.android.core.models.Survey.cardTitle(): String =
    when (state) {
        is Evaluation -> stringResource(R.string.surveys_cardtitle_evaluation)
        is Participation -> stringResource(R.string.surveys_cardtitle_participation)
    }

@Composable
fun de.cleema.android.core.models.Survey.buttonTitle(): String =
    when (state) {
        is Evaluation -> stringResource(R.string.surveys_action_evaluation)
        is Participation -> stringResource(R.string.surveys_action_participation)
    }

val de.cleema.android.core.models.SurveyState.uri: String
    get() = when (this) {
        is Evaluation -> uri
        is Participation -> uri
    }

@Preview(widthDp = 340)
@Composable
fun SurveyCardParticipationPreview() {
    CleemaTheme {
        SurveyCard(
            survey = de.cleema.android.core.models.Survey(
                id = UUID.randomUUID(),
                title = "Hallo Umfrage",
                description = "Umfragebeschreibung folgt",
                state = Participation("https://cleema.app")
            ),
            onSurveyClicked = {}
        )
    }
}

@Preview(widthDp = 340)
@Composable
fun SurveyCardEvaluationPreview() {
    CleemaTheme {
        SurveyCard(
            survey = de.cleema.android.core.models.Survey(
                id = UUID.randomUUID(),
                title = "Hallo Auswertung",
                description = "Auswertung verfügbar",
                state = Evaluation("https://cleema.app")
            ),
            onSurveyClicked = {}
        )
    }
}
