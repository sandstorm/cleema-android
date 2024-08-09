/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.BuildConfig
import de.cleema.android.R
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed
import de.cleema.android.navigation.InfoContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDrawer(
    content: List<InfoContent>,
    onClick: (DrawerRoute) -> Unit,
    onProfileButtonClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = DefaultText,
    ) {
        UserDrawerItem(modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .clickable {
                onProfileButtonClick()
            }
        )

        content.forEach { item ->
            if (item.enabled) {
                NavigationDrawerItem(
                    label = {
                        Text(
                            stringResource(id = item.route.titleRes),
                            color = Accent,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    selected = false,
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        onClick(item.route)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = DefaultText,
                        unselectedContainerColor = DefaultText
                    )
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 28.dp)
                ) {
                    Text(
                        stringResource(id = item.route.titleRes),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        stringResource(R.string.local_user_sponsor_hint),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            modifier = Modifier.padding(8.dp),
            color = Dimmed,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
