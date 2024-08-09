/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.HiltAndroidApp
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.drawer.InfoDrawer
import de.cleema.android.drawer.navigateTo
import de.cleema.android.navigation.*
import de.cleema.android.profile.navigateToProfileHome
import kotlinx.coroutines.launch

@HiltAndroidApp
class CleemaApp : Application()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    viewModel: AppNavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            InfoDrawer(
                content = uiState.infoDrawerContent,
                onClick = {
                    scope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        }
                    }
                    viewModel.select(it)
                    navController.navigateTo(it)
                },
                onProfileButtonClick = {
                    scope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        }
                        navController.navigateToProfileHome()
                        viewModel.openProfile()
                    }
                }
            )
        },
        content = {
            Scaffold(
                modifier = modifier
                    .fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = DefaultText,
                            navigationIconContentColor = Accent
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Default.Menu,
                                    contentDescription = stringResource(R.string.open_menu_button_ax_description)
                                )
                            }
                        },
                        title = {
                            Image(
                                painter = painterResource(id = R.drawable.cleema_logo_redesign_tuerkis),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(scaleX = 0.25f, scaleY = 0.25f)
                                    .absoluteOffset(x = -(100.dp))
                            )
                        },
                        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                    )
                },
                bottomBar = {
                    CleemaBottomNavigation(
                        allScreens = AppScreens.values().toList(),
                        onTabSelected = {
                            if (currentDestination?.route != it.startRoute) {
                                if (currentDestination?.route?.startsWith(it.route) == true) {
                                    navController.backTo(it.route)
                                } else {
                                    navController.navigateSingleTopTo(it.route)
                                }
                                viewModel.selectScreen(it)
                            }
                        },
                        currentScreen = uiState.screen,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            ) { innerPadding ->
                CleemaNavHost(
                    navController,
                    Modifier
                        .padding(innerPadding)
                )
            }
        }
    )
}
