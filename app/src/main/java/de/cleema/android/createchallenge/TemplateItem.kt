/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.createchallenge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.core.models.Size
import de.cleema.android.core.styling.CleemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateItem(
    title: String,
    teaserText: String,
    modifier: Modifier = Modifier,
    logo: de.cleema.android.core.models.RemoteImage? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        contentColor = Color.White,
        shadowElevation = 16.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(text = teaserText, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.weight(1f))

            logo?.let { image ->
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    de.cleema.android.core.components.RemoteImageView(
                        remoteImage = image,
                        modifier = Modifier
                            .size((image.size.width * 20 / image.size.height).dp, 20.dp)
//                        .align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 160, widthDp = 160)
@Composable
fun TemplateItemPreview() {
    CleemaTheme {
        TemplateItem(
            title = "Erkunde die App",
            teaserText = "Das ist ein Teasertext.",
            modifier = Modifier.padding(10.dp),
            logo = de.cleema.android.core.models.RemoteImage.of(
                "https://cleema.app/uploads/Logo_Stadtverwaltung_SW_6aec3666bd.jpg",
                Size(width = 60f, height = 20f)
            )
        ) {}
    }
}
