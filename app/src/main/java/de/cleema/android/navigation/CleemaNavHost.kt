/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import de.cleema.android.BuildConfig
import de.cleema.android.challenges.challengesGraph
import de.cleema.android.core.models.DeepLinking
import de.cleema.android.dashboard.DashboardGraphRoutePattern
import de.cleema.android.dashboard.dashboardGraph
import de.cleema.android.drawer.drawerDialog
import de.cleema.android.magazine.magazineGraph
import de.cleema.android.marketplace.marketplaceGraph
import de.cleema.android.profile.profileDialog
import de.cleema.android.projects.projectsGraph
import de.cleema.android.social.FollowInvitationDialog

@Composable
fun CleemaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DashboardGraphRoutePattern,
        modifier = modifier
    )
    {
        dashboardGraph(navController)

        magazineGraph(navController)

        projectsGraph(navController)

        challengesGraph(navController)

        marketplaceGraph(navController)

        drawerDialog(navController)

        deepLinking(navController)

        profileDialog(navController)
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.backTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@backTo.graph.findStartDestination().id
        ) {
            saveState = false
        }
        launchSingleTop = false
        restoreState = false
    }

const val profileRoute = "profile"

internal fun NavGraphBuilder.deepLinking(navController: NavController) {
    dialog(
        "$profileRoute?id={${DeepLinking.invitationCode}}",
        deepLinks = listOf(navDeepLink {
            uriPattern = "${BuildConfig.baseUrl}/invites/{${DeepLinking.invitationCode}}"
        })
    ) {
        FollowInvitationDialog(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            navController.popBackStack()
        }
    }
}
