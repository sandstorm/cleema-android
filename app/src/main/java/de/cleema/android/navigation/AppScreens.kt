/*
 * Created by Kumpels and Friends on 2022-11-14
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.cleema.android.R
import de.cleema.android.challenges.ChallengesGraphRoutePattern
import de.cleema.android.challenges.ChallengesListRoutePattern
import de.cleema.android.dashboard.DashboardGraphRoutePattern
import de.cleema.android.dashboard.DashboardGridRoutePattern
import de.cleema.android.magazine.MagazineGraphRoutePattern
import de.cleema.android.magazine.MagazineListRoutePattern
import de.cleema.android.marketplace.MarketplaceGraphRoutePattern
import de.cleema.android.marketplace.MarketplaceListRoutePattern
import de.cleema.android.projects.ProjectsGraphRoutePattern
import de.cleema.android.projects.ProjectsListRoutePattern

enum class AppScreens(
    val route: String,
    val startRoute: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
) {
    Dashboard(
        route = DashboardGraphRoutePattern,
        startRoute = DashboardGridRoutePattern,
        R.string.home_tab_name,
        R.drawable.home_tab_icon
    ),

    News(
        route = MagazineGraphRoutePattern,
        startRoute = MagazineListRoutePattern,
        R.string.news_tab_name,
        R.drawable.news_tab_icon
    ),

    Projects(
        route = ProjectsGraphRoutePattern,
        startRoute = ProjectsListRoutePattern,
        R.string.projects_tab_name,
        R.drawable.projects_tab_icon
    ),

    Challenges(
        route = ChallengesGraphRoutePattern,
        startRoute = ChallengesListRoutePattern,
        R.string.challenges_tab_name,
        R.drawable.challenges_tab_icon
    ),

    Marketplace(
        route = MarketplaceGraphRoutePattern,
        startRoute = MarketplaceListRoutePattern,
        R.string.marketplace_tab_name,
        R.drawable.marketplace_tab_icon
    )
}
