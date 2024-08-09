/*
 * Created by Kumpels and Friends on 2022-11-17
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.cleema.android.core.styling.Action

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteButton(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: () -> Unit
) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false,
    ) {
        IconToggleButton(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            modifier = modifier
        ) {
            Icon(
                painter = if (checked) painterResource(id = R.drawable.ic_star) else painterResource(
                    id = R.drawable.ic_star_outline
                ),
                contentDescription = if (checked) stringResource(id = R.string.add_favorite_description) else stringResource(
                    id = R.string.remove_favorite_description
                ),
                tint = Action
            )
        }
    }
}
