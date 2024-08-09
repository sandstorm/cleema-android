/*
 * Created by Kumpels and Friends on 2022-12-09
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.components.BadgeView
import java.text.MessageFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialItemCard(followerCount: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(19.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.wave),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(R.string.dashboard_invite_title),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                val fmt = stringResource(R.string.dashboard_invite_follower_count)
                Text(
                    MessageFormat.format(fmt, followerCount),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            de.cleema.android.core.components.BadgeView(text = "$followerCount")
        }
    }
}

@Preview(widthDp = 130)
@Composable
fun SocialItemCardPreview() {
    SocialItemCard(followerCount = 4) {}
}
