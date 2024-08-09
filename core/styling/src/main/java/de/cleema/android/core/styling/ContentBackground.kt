/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.styling

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize

@Composable
fun ContentBackground(modifier: Modifier = Modifier) {
    var boxSize by remember { mutableStateOf(Size.Zero) }
    Box(modifier = modifier
        .onSizeChanged { boxSize = it.toSize() }
        .height(IntrinsicSize.Min)
        .fillMaxWidth()
        .background(DefaultText)
        .drawBehind {
            drawRect(
                size = Size(width = boxSize.width, height = boxSize.height / 2),
                color = Accent,
                topLeft = Offset(x = 0f, y = boxSize.height / 2)
            )
        },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.background0),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.background1),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.background2),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.background3),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.background4),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(
    name = "Background",
    widthDp = 500,
    heightDp = 1000
)
@Composable
private fun ContentBackgroundPreview() {
    CleemaTheme {
        ContentBackground()
    }
}
