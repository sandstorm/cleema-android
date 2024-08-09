/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.partnerchallenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.Light

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerInfo(
    partner: de.cleema.android.core.models.Partner,
    modifier: Modifier = Modifier,
    onOpenPartnerClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Light
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.partner_heading), style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.weight(1f))
                partner.logo?.let {
                    de.cleema.android.core.components.RemoteImageView(
                        remoteImage = it,
                        modifier = Modifier.size(100.dp, 36.dp)
                    )
                }
            }

            CompositionLocalProvider(
                LocalMinimumTouchTargetEnforcement provides true,
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                Text(
                    partner.name, modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onOpenPartnerClick
                        ), color = Action, style = MaterialTheme.typography.titleMedium
                )
            }

            Text(text = partner.description, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PartnerInfoPreview() {
    CleemaTheme {
        PartnerInfo(
            partner = de.cleema.android.core.models.Partner(
                name = "LVB",
                url = "http://lvb.de",
                description = LoremIpsum(20).values.joinToString(" ")
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onOpenPartnerClick = { /*TODO*/ })
    }
}
