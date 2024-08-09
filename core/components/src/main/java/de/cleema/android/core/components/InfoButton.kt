/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlin.math.roundToInt

data class DpOffset(val x: Dp, val y: Dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoButton(
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 24.dp),
    icon: @Composable () -> Unit = {
        Icon(Icons.Outlined.Info, contentDescription = null, tint = LightGray)
    },
    content: @Composable () -> Unit
) {
    var infoVisible by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        Box(modifier) {
            IconButton(onClick = { infoVisible = !infoVisible }, content = icon)

            if (infoVisible) {
                val density = LocalDensity.current.density
                val intOffset =
                    IntOffset((offset.x.value * density).roundToInt(), (offset.y.value * density).roundToInt())

                Popup(onDismissRequest = { infoVisible = false }, offset = intOffset) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        content = content,
                        shadowElevation = 8.dp
                    )
                }
            }
        }
    }

}
