/*
 * Created by Kumpels and Friends on 2022-11-09
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.styling

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleemaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 4.dp),
    content: @Composable RowScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        TextButton(
            colors = ButtonDefaults.textButtonColors(
                contentColor = Action
            ),
            onClick = onClick,
            modifier = modifier.defaultMinSize(minWidth = 20.dp, minHeight = 20.dp),
            contentPadding = contentPadding,
            content = content,
            shape = RectangleShape
        )
    }
}

@Preview
@Composable
private fun CleemaTextButtonPreview() {
    CleemaTheme {
        CleemaTextButton(onClick = {}) {
            Text("Click me")
        }
    }
}
