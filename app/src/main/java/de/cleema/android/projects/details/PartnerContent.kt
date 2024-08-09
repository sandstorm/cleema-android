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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTextButton
import de.cleema.android.core.styling.Light

@Composable
fun PartnerContent(
    partner: de.cleema.android.core.models.Partner,
    onPartnerClicked: () -> Unit
) {
    Surface(color = Light, modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(id = R.string.project_detail_partner_link_label))

            CleemaTextButton(
                onClick = onPartnerClicked,
                contentPadding = PaddingValues()
            ) {
                Text(partner.name, style = MaterialTheme.typography.titleLarge, color = Action)
            }

            de.cleema.android.core.components.MarkdownText(partner.description)
        }
    }
}
