/*
 * Created by Kumpels and Friends on 2023-01-13
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed

@Composable
fun SponsorItem(
    content: de.cleema.android.core.models.SponsorPackage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            content.image?.let {
                de.cleema.android.core.components.RemoteImageView(remoteImage = it)
            } ?: Icon(
                painter = painterResource(id = content.type.logo),
                contentDescription = stringResource(R.string.sponsor_ax_image_descripion),
                modifier = modifier
                    .background(color = DefaultText, shape = CircleShape)
                    .size(72.dp)
                    .padding(16.dp),
                tint = Color.White
            )

            Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
                Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = DefaultText)) {
                        append(stringResource(R.string.sponsor_cleema))
                    }

                    withStyle(style = SpanStyle(color = Dimmed)) {
                        append(content.title)
                    }
                }, style = MaterialTheme.typography.headlineSmall)

                Text(content.description, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Text(
            stringResource(id = R.string.become_sponsor_confirm_price, content.price),
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Action, shape = SponsorRect)
                .padding(vertical = 16.dp)
        )
    }
}

val de.cleema.android.core.models.SponsorType.logo: Int
    get() = when (this) {
        de.cleema.android.core.models.SponsorType.FAN -> R.drawable.icon_fan
        de.cleema.android.core.models.SponsorType.MAKER -> R.drawable.icon_maker
        de.cleema.android.core.models.SponsorType.LOVE -> R.drawable.icon_love
    }


@Preview(showBackground = true)
@Composable
fun SponsorItemPreview() {
    CleemaTheme {
        SponsorItem(de.cleema.android.core.models.SponsorPackage.fan)
    }
}
