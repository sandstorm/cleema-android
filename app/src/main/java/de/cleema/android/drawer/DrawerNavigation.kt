/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.drawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.*
import de.cleema.android.R
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText

const val DrawerGraphRoutePattern = "drawer"
const val drawerRouteArg = "drawerRouteArg"

private fun NavGraphBuilder.drawerContent(
    viewModel: DrawerScreenViewModel,
    dismiss: () -> Unit
) {
    navigation(
        startDestination = "$DrawerGraphRoutePattern/{$drawerRouteArg}",
        route = DrawerGraphRoutePattern,
    ) {
        composable(
            "$DrawerGraphRoutePattern/{$drawerRouteArg}",
            arguments = listOf(
                navArgument(drawerRouteArg) {
                    type = NavType.StringType
                }
            )
        ) {
            DrawerScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                onDismiss = dismiss
            )
        }
    }
}

internal fun NavController.navigateTo(route: DrawerRoute) {
    this.navigate("$DrawerGraphRoutePattern/${route.name}")
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
fun NavGraphBuilder.drawerDialog(
    parentNavController: NavController
) {
    dialog(
        "$DrawerGraphRoutePattern/{$drawerRouteArg}",
        arguments = listOf(
            navArgument(drawerRouteArg) {
                type = NavType.StringType
            }
        ),
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
    ) {
        val navController = rememberNavController(DialogNavigator())
        val vm: DrawerScreenViewModel = hiltViewModel(it)
        val state by vm.uiState.collectAsStateWithLifecycle()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Accent,
                        navigationIconContentColor = DefaultText
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.currentDestination?.route?.let {
                                navController.clearBackStack(it)
                            }
                            navController.popBackStack()
                            parentNavController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close_button_ax_description)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(
                                id = state.route.titleRes
                            ),
                            modifier = Modifier
                                .padding(horizontal = 20.dp),
                            color = DefaultText,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DrawerGraphRoutePattern,
                modifier = Modifier.padding(innerPadding)
            ) {
                drawerContent(viewModel = vm) {
                    navController.popBackStack()
                    parentNavController.popBackStack()
                }
            }
        }
    }
}
