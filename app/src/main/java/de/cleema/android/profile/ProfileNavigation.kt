/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.*
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.magazine.magazineItemScreen
import de.cleema.android.projects.projectScreen
import java.util.*

const val ProfileGraphRoutePattern = "profile"
const val ProfileHomeRoute = "$ProfileGraphRoutePattern/profile"
const val ProfileProjectRoute = "$ProfileGraphRoutePattern/project"
const val ProfileMagazineItemRoute = "$ProfileGraphRoutePattern/magazineItem"
const val ProfileTrophyRoute = "$ProfileGraphRoutePattern/trophy"
const val ProfileSocialUserListRoute = "$ProfileGraphRoutePattern/socialUsers"
const val FollowersRoute = "$ProfileSocialUserListRoute/followers"
const val FollowingsRoute = "$ProfileSocialUserListRoute/following"
const val trophyIdArg = "trophyId"
const val EditProfileRoute = "$ProfileGraphRoutePattern/edit"

fun NavGraphBuilder.profileGraph(navController: NavController, profileViewModel: ProfileViewModel) {
    navigation(
        startDestination = ProfileHomeRoute,
        route = ProfileGraphRoutePattern
    ) {
        profileScreen(
            profileViewModel = profileViewModel,
            onProjectClick = {
                navController.navigateToProject(it)
            },
            onMagazineItemClick = {
                navController.navigateToMagazineItem(it)
            },
            onTrophyClick = {
                navController.navigateToTrophy(it)
            },
            onFollowersClick = {
                navController.navigateToFollowers()
            },
            onFollowingsClick = {
                navController.navigateToFollowings()
            }
        )

        projectScreen(ProfileProjectRoute, showBackground = false)

        magazineItemScreen(ProfileMagazineItemRoute, showBackground = false)

        trophyScreen()

        socialUserListScreen()
    }
}

fun NavGraphBuilder.profileScreen(
    profileViewModel: ProfileViewModel,
    onProjectClick: (UUID) -> Unit,
    onMagazineItemClick: (UUID) -> Unit,
    onTrophyClick: (UUID) -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingsClick: () -> Unit,
) {
    composable(
        ProfileHomeRoute,
    ) {
        ProfileScreen(
            profileViewModel = profileViewModel,
            onProjectClick = onProjectClick,
            onMagazineItemClick = onMagazineItemClick,
            onTrophyClick = onTrophyClick,
            onFollowersClick = onFollowersClick,
            onFollowingsClick = onFollowingsClick
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
fun NavGraphBuilder.profileDialog(parentNavController: NavController) {
    dialog(
        ProfileHomeRoute,
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
    ) {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val profileViewModel = hiltViewModel<ProfileViewModel>()
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
                        NavigationIcon(currentBackStack, parentNavController, navController, profileViewModel)
                    },
                    title = {
                        screenTitle(currentBackStack)?.let { title ->
                            Text(
                                text = title,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp),
                                color = DefaultText,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    },
                    scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                    actions = {
                        Actions(currentBackStack, profileViewModel)
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ProfileGraphRoutePattern,
                modifier = Modifier.padding(innerPadding)
            )
            {
                profileGraph(navController, profileViewModel)
            }
        }
    }
}


@Composable
fun Actions(backStack: NavBackStackEntry?, profileViewModel: ProfileViewModel) {
    val focusManager = LocalFocusManager.current
    when (backStack?.destination?.route) {
        ProfileHomeRoute -> {
            val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
            when (uiState) {
                is ProfileUiState.Content ->
                    IconButton(
                        onClick = profileViewModel::startEditing,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = DefaultText)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.profile_edit_title),
                        )
                    }
                is ProfileUiState.Edit ->
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        profileViewModel.saveEditing()
                    }) {
                        Text(
                            stringResource(R.string.profile_edit_user_save_button),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                ProfileUiState.Loading -> return
            }
        }
    }
}


@Composable
fun NavigationIcon(
    backStack: NavBackStackEntry?,
    parentNavController: NavController,
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    when (backStack?.destination?.route) {
        ProfileHomeRoute -> {
            val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
            when (uiState) {
                is ProfileUiState.Content ->
                    IconButton(onClick = {
                        parentNavController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.profile_close_button_description)
                        )
                    }
                is ProfileUiState.Edit ->
                    TextButton(onClick = profileViewModel::cancelEditing) {
                        Text(
                            stringResource(R.string.profile_edit_cancel_button_title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                ProfileUiState.Loading -> return
            }
        }

        else -> {
            backStack?.destination?.route?.let { route ->
                val routes = listOf(
                    ProfileTrophyRoute,
                    ProfileProjectRoute,
                    ProfileMagazineItemRoute,
                    FollowersRoute,
                    FollowingsRoute
                )
                if (routes.any { route.startsWith(it) }) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.profile_back_button_title)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun screenTitle(
    backStack: NavBackStackEntry?
): String? = when (backStack?.destination?.route) {
    ProfileHomeRoute -> stringResource(R.string.profile_title)
    FollowersRoute -> stringResource(R.string.social_user_type_followers)
    FollowingsRoute -> stringResource(R.string.social_user_type_following)
    else -> null
}

private fun NavController.navigateToProject(projectId: UUID) {
    this.navigate("$ProfileProjectRoute/$projectId", navOptions { this.launchSingleTop = true })
}

private fun NavController.navigateToMagazineItem(magazineItemId: UUID) {
    this.navigate("$ProfileMagazineItemRoute/$magazineItemId", navOptions { this.launchSingleTop = true })
}

private fun NavController.navigateToTrophy(trophyId: UUID) {
    this.navigate("$ProfileTrophyRoute/$trophyId", navOptions { this.launchSingleTop = true })
}

private fun NavController.navigateToFollowers() {
    this.navigate(FollowersRoute, navOptions { this.launchSingleTop = true })
}

private fun NavController.navigateToFollowings() {
    this.navigate(FollowingsRoute, navOptions { this.launchSingleTop = true })
}

fun NavController.navigateToProfileHome() {
    this.navigate(ProfileHomeRoute)
}

fun NavGraphBuilder.trophyScreen() {
    composable(
        "$ProfileTrophyRoute/{$trophyIdArg}",
        arguments = listOf(
            navArgument(trophyIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        TrophyScreen()
    }
}

fun NavGraphBuilder.socialUserListScreen() {
    composable(FollowersRoute) {
        SocialUserListScreen(true)
    }

    composable(FollowingsRoute) {
        SocialUserListScreen(showsFollowers = false)
    }
}

