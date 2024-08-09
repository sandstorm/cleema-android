/*
 * Created by Kumpels and Friends on 2023-01-03
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import de.cleema.android.core.styling.Action

private const val HORIZONTAL_OFFSET = 5
private const val VERTICAL_OFFSET = 5
private val MIN_SIZE = Size(39f, 37f)
private const val HORIZONTAL_PADDING = 26
private const val VERTICAL_PADDING = 10

@Composable
fun BadgeView(text: String, color: Color = Action) {
    val density = LocalDensity.current
    var textSize by remember { mutableStateOf(Size.Zero) }

    Box(contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        val (w, h) = with(density) {
            max(
                MIN_SIZE.width.dp, textSize.width.toDp() + de.cleema.android.core.components.HORIZONTAL_PADDING.dp
            ).roundToPx() to max(
                MIN_SIZE.height.dp, textSize.height.toDp() + de.cleema.android.core.components.VERTICAL_PADDING.dp
            ).roundToPx()
        }
        ContextCompat.getDrawable(context, R.drawable.badge)?.toBitmap(w, h)?.asImageBitmap()?.let {
            Image(it, contentDescription = null, colorFilter = ColorFilter.tint(color))
        }


        Text(text = text,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    textSize = coordinates.size.toSize()
                }
                .offset(HORIZONTAL_OFFSET.dp, (-VERTICAL_OFFSET).dp))
    }
}

@Preview
@Composable
fun BadgeViewPreview() {
    BadgeView(text = "4")
}
