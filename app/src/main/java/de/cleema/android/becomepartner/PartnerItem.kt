/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomepartner

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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.cleema.android.R
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed

@Composable
fun PartnerItem(
    content: de.cleema.android.core.models.PartnerPackage,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            content.image?.let {
                de.cleema.android.core.components.RemoteImageView(remoteImage = it)
            } ?: Icon(
                painter = painterResource(id = content.type.symbol),
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
                }, style = MaterialTheme.typography.headlineSmall, fontSize = 18.sp)

                Text(content.copy, style = MaterialTheme.typography.bodyLarge, fontSize = 18.sp)
            }
        }
    }
}

val de.cleema.android.core.models.PartnerType.symbol: Int
    get() = when (this) {
        de.cleema.android.core.models.PartnerType.STARTER -> R.drawable.icon_rocket
        de.cleema.android.core.models.PartnerType.DARLINGS -> R.drawable.icon_love
        de.cleema.android.core.models.PartnerType.LEARNING -> R.drawable.icon_learning
    }

@Preview(showBackground = true)
@Composable
fun StarterPreview() {
    CleemaTheme {
        PartnerItem(de.cleema.android.core.models.PartnerPackage.starter)
    }
}

@Preview(showBackground = true)
@Composable
fun DarlingsPreview() {
    CleemaTheme {
        PartnerItem(de.cleema.android.core.models.PartnerPackage.darlings)
    }
}

@Preview(showBackground = true)
@Composable
fun PartnerPreview() {
    CleemaTheme {
        PartnerItem(de.cleema.android.core.models.PartnerPackage.partner)
    }
}
