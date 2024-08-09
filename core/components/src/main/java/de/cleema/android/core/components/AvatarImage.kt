/*
 * Created by Kumpels and Friends on 2022-12-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.cleema.android.core.models.RemoteImage

@Composable
fun AvatarImage(
    image: RemoteImage?,
    modifier: Modifier = Modifier,
    size: Dp = 104.dp
) {
    image?.let {
        RemoteImageView(
            remoteImage = it, contentScale = ContentScale.Crop, modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } ?: Icon(
        painter = painterResource(id = R.drawable.avatar_placeholder),
        contentDescription = stringResource(id = R.string.avatar_placeholder),
        modifier = modifier.background(color = Color.LightGray, shape = CircleShape),
        tint = Color.White
    )
}
