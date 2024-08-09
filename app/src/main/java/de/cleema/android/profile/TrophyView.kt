/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.cleema.android.core.components.RemoteImageView
import de.cleema.android.core.models.Trophy
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TrophyView(trophy: de.cleema.android.core.models.Trophy, modifier: Modifier = Modifier) {
    Box(
        modifier.background(Color.Transparent)
    ) {
        de.cleema.android.core.components.RemoteImageView(
            remoteImage = trophy.image,
            modifier = Modifier.size(104.dp),
            contentScale = ContentScale.Fit
        )

        Column(Modifier.fillMaxSize()) {
            Row {
                Box(Modifier.size(76.dp))

                Column(
                    modifier = Modifier
                        .width(28.dp)
                        .padding(horizontal = 2.dp)
                        .padding(top = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        trophy.date.toLocalDateTime(TimeZone.UTC).monthNumber.toString().padStart(2, '0'),
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall
                    )

                    Divider(color = Color.White)

                    Text(
                        trophy.date.toLocalDateTime(TimeZone.UTC).year.toString().drop(2),
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    trophy.title,
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .height(28.dp)
                        .fillMaxWidth()
                        .padding(start = 7.dp, end = 7.dp)
                )
            }
        }
    }
}
