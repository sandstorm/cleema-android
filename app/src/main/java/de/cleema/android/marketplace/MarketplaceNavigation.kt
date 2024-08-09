/*
 * Created by Kumpels and Friends on 2022-11-14
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.composable
import java.util.*

const val MarketplaceGraphRoutePattern = "marketplace"
internal const val MarketplaceListRoutePattern = "$MarketplaceGraphRoutePattern/list"
private const val marketplaceIdArg = "marketplaceId"

fun NavGraphBuilder.marketplaceGraph(navController: NavController) {
    navigation(
        startDestination = MarketplaceListRoutePattern,
        route = MarketplaceGraphRoutePattern
    ) {
        marketplaceScreen(
            onNavigationToOffer = { id ->
                navController.navigateToOffer(id)
            }
        )

        offerScreen()
    }
}

fun NavGraphBuilder.marketplaceScreen(
    onNavigationToOffer: (offerId: UUID) -> Unit
) {
    composable(MarketplaceListRoutePattern) {
        MarketplaceScreen(
            modifier = Modifier.fillMaxSize()
        ) { onNavigationToOffer(it) }
    }
}

fun NavGraphBuilder.offerScreen() {
    composable(
        "$MarketplaceGraphRoutePattern/{$marketplaceIdArg}",
        arguments = listOf(
            navArgument(marketplaceIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        OfferScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}

fun NavController.navigateToOffer(offerId: UUID) {
    this.navigate("$MarketplaceGraphRoutePattern/$offerId")
}

