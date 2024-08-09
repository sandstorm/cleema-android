/*
 * Created by Kumpels and Friends on 2022-11-14
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.*

@Composable
fun ProjectPhase(
    project: de.cleema.android.core.models.Project,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = project.phase.backgroundColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.project_detail_phase_label),
                fontWeight = FontWeight.SemiBold,
                color = project.phase.textColor
            )
            Text(project.formattedProjectText(), color = project.phase.textColor)
        }
    }
}

val de.cleema.android.core.models.Phase.backgroundColor: Color
    get() = when (this) {
        de.cleema.android.core.models.Phase.Pre -> Light
        de.cleema.android.core.models.Phase.Cancelled -> FinishedProject
        de.cleema.android.core.models.Phase.Post -> FinishedProject
        de.cleema.android.core.models.Phase.Within -> Dimmed
    }
val de.cleema.android.core.models.Phase.textColor: Color
    get() = when (this) {
        de.cleema.android.core.models.Phase.Pre -> DefaultText
        de.cleema.android.core.models.Phase.Cancelled -> Color.White
        de.cleema.android.core.models.Phase.Post -> Color.White
        de.cleema.android.core.models.Phase.Within -> Color.White
    }

@Preview("Project phase", widthDp = 320)
@Composable
fun ProjectPrePhasePreview() {
    CleemaTheme {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProjectPhase(
                project = de.cleema.android.core.models.Project(
                    region = de.cleema.android.core.models.Region.LEIPZIG,
                    partner = de.cleema.android.core.models.Partner(name = "Partner"),
                    phase = de.cleema.android.core.models.Phase.Pre
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ProjectPhase(
                project = de.cleema.android.core.models.Project(
                    region = de.cleema.android.core.models.Region.LEIPZIG,
                    partner = de.cleema.android.core.models.Partner(name = "Partner"),
                    phase = de.cleema.android.core.models.Phase.Within
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ProjectPhase(
                project = de.cleema.android.core.models.Project(
                    region = de.cleema.android.core.models.Region.LEIPZIG,
                    partner = de.cleema.android.core.models.Partner(name = "Partner"),
                    phase = de.cleema.android.core.models.Phase.Post
                ),
                modifier = Modifier.fillMaxWidth()
            )

            ProjectPhase(
                project = de.cleema.android.core.models.Project(
                    region = de.cleema.android.core.models.Region.LEIPZIG,
                    partner = de.cleema.android.core.models.Partner(name = "Partner"),
                    phase = de.cleema.android.core.models.Phase.Cancelled
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
