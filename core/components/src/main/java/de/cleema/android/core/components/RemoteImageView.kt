/*
 * Created by Kumpels and Friends on 2022-11-18
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RemoteImageView(
    remoteImage: de.cleema.android.core.models.RemoteImage?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alpha: Float = DefaultAlpha,
) {
    val painter = painterResource(R.drawable.ic_image_placeholder)
    val placeholder: (drawBackground: Boolean) -> Painter = { drawBackground ->
        forwardingPainter(
            painter = painter,
            colorFilter = ColorFilter.tint(Color.LightGray),
            alpha = 1f
        ) { info ->
            if (drawBackground) {
                drawRect(Color.LightGray.copy(alpha = 0.25f))
            }
            scale(0.25f) {
                with(info.painter) {
                    draw(size, info.alpha, info.colorFilter)
                }
            }
        }
    }
    remoteImage?.let {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(it.url)
                .crossfade(true)
                .build(),
            placeholder = placeholder(true),
            alignment = alignment,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier.size(it.size.width.dp, it.size.height.dp),
            colorFilter = colorFilter,
            alpha = alpha
        )
    } ?: androidx.compose.foundation.Image(
        painter = placeholder(false),
        contentDescription = null,
        modifier = modifier.background(Color.LightGray.copy(alpha = 0.25f)),
        colorFilter = colorFilter
    )
}
