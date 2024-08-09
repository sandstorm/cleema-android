/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.magazine

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.composable
import de.cleema.android.magazine.details.MagazineItemContainer
import de.cleema.android.magazine.details.MagazineItemContent
import de.cleema.android.magazine.list.MagazineScreen
import java.util.*

const val MagazineGraphRoutePattern = "magazine"
internal const val MagazineListRoutePattern = "$MagazineGraphRoutePattern/list"
private const val newsIdArg = "newsId"

fun NavGraphBuilder.magazineGraph(navController: NavController) {
    navigation(
        startDestination = MagazineListRoutePattern,
        route = MagazineGraphRoutePattern
    ) {
        magazineScreen(
            onNavigationToNews = { id ->
                navController.navigateToNews(id)
            }
        )

        magazineItemScreen()
    }
}

fun NavGraphBuilder.magazineScreen(
    onNavigationToNews: (newsId: UUID) -> Unit
) {
    composable(MagazineListRoutePattern) {
        MagazineScreen(
            Modifier.fillMaxSize(),
            onNavigateToItem = { onNavigationToNews(it) }
        )
    }
}

fun NavGraphBuilder.magazineItemScreen(baseRoute: String = MagazineGraphRoutePattern, showBackground: Boolean = true) {
    composable(
        "$baseRoute/{$newsIdArg}",
        arguments = listOf(
            navArgument(newsIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        if (showBackground) {
            MagazineItemContainer(modifier = Modifier.fillMaxSize())
        } else {
            MagazineItemContent()
        }
    }
}

fun NavController.navigateToNews(newsID: UUID) {
    this.navigate("$MagazineGraphRoutePattern/$newsID")
}
