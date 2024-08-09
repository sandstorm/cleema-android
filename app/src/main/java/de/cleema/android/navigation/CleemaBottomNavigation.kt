/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed
import de.cleema.android.navigation.AppScreens.*

@Composable
fun CleemaBottomNavigation(
    allScreens: List<AppScreens>,
    onTabSelected: (AppScreens) -> Unit,
    currentScreen: AppScreens,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier,
        containerColor = Accent,
        contentColor = DefaultText,
        windowInsets = WindowInsets.navigationBars
    ) {
        listOf(Dashboard, News, Projects, Challenges, Marketplace).forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = null,
                        //modifier = Modifier.height(20.dp)
                    )
                },
                label = {
                    Text(
                        stringResource(screen.title),
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        color = if (screen == currentScreen) DefaultText else Dimmed
                    )
                },
                selected = screen == currentScreen,
                onClick = { onTabSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Accent,
                    selectedTextColor = DefaultText,
                    selectedIconColor = DefaultText,
                    unselectedTextColor = Dimmed,
                    unselectedIconColor = Dimmed
                )
            )
        }
    }
}
