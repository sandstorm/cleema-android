/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.DefaultText

@Composable
fun UserAvatar(
    user: de.cleema.android.core.models.User,
    avatarDefaultColor: Color = DefaultText,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(contentAlignment = Alignment.BottomEnd) {
            user.avatar?.let { avatar ->
                de.cleema.android.core.components.RemoteImageView(
                    remoteImage = avatar.image, contentScale = ContentScale.Crop, modifier = Modifier
                        .size(104.dp)
                        .clip(CircleShape)
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = stringResource(id = R.string.avatar_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(104.dp)
                    .border(BorderStroke(6.dp, avatarDefaultColor), CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape),
                colorFilter = ColorFilter.tint(avatarDefaultColor)
            )

            if (user.isSupporter) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .padding(bottom = 2.dp)
                        .offset(x = 2.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp, end = 4.dp)
                            .clip(CircleShape)
                            .background(DefaultText)
                            .size(18.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.verified),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}
