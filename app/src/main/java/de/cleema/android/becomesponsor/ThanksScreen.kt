/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText

@Composable
fun ThanksScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.become_sponsor_thanks_headline), style = typography.titleMedium)

        Row(
            horizontalArrangement = spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = CenterVertically
        ) {
            Icon(painter = painterResource(R.drawable.favorite), contentDescription = null, tint = DefaultText)
            Text(
                stringResource(R.string.become_sponsor_thanks_body),
                style = typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThanksScreenPreview() {
    CleemaTheme {
        ThanksScreen()
    }
}
