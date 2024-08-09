/*
 * Created by Kumpels and Friends on 2022-11-14
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action

@Composable
fun InvolvementGoalView(involvement: de.cleema.android.core.models.Goal.Involvement, onJoinClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painterResource(R.drawable.people),
            contentDescription = stringResource(id = R.string.involvement_image_description)
        )

        val text: String = if (involvement.joined) {
            stringResource(
                id = R.string.involvement_summary_hasTotal_joined,
                involvement.currentParticipants - 1,
                involvement.maxParticipants
            )
        } else {
            stringResource(
                id = R.string.involvement_summary_hasTotal,
                involvement.currentParticipants,
                involvement.maxParticipants
            )
        }

        Text(text, modifier = Modifier.weight(0.5f))

        Button(
            onClick = { onJoinClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Action),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                stringResource(id = if (involvement.joined) R.string.involvement_action_leave_label else R.string.involvement_action_join_label),
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Preview(name = "Involvement View")
@Composable
fun InvolvementGoalViewPreview() {
    var involvement: de.cleema.android.core.models.Goal.Involvement by remember {
        mutableStateOf(de.cleema.android.core.models.Goal.Involvement(42, 100, false))
    }
    InvolvementGoalView(
        involvement = involvement,
        onJoinClick = {
            involvement =
                involvement.copy(joined = involvement.joined.not())
        }
    )
}
