/*
 * Created by Kumpels and Friends on 2023-01-03
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.cleema.android.core.styling.Dimmed

@Composable
fun FavoriteButton(
    title: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Dimmed
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, Modifier.alpha(0.66f))
        Spacer(Modifier.width(12.dp))
        Text(text = title, color = Color.White, maxLines = 2)
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
    }
}
