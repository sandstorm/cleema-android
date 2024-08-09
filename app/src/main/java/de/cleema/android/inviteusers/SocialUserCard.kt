/*
 * Created by Kumpels and Friends on 2022-12-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.inviteusers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText

@Composable
fun SocialUserCard(
    image: de.cleema.android.core.models.RemoteImage?,
    name: String,
    checked: Boolean,
    showsDivider: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            de.cleema.android.core.components.AvatarImage(image, size = 50.dp)
            Text(
                name,
                style = MaterialTheme.typography.bodyLarge,
                color = DefaultText
            )

            if (checked) {
                Spacer(Modifier.weight(1f))
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = DefaultText)
            }
        }
        if (showsDivider) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview("Social user card", widthDp = 320)
@Composable
fun SocialUserCardPreview() {
    CleemaTheme {
        SocialUserCard(
            image = null,
            name = "Clara Cleema",
            checked = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium),
            showsDivider = false,
            onClick = {}
        )
    }
}
