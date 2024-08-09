package de.cleema.android.challenges

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import de.cleema.android.createchallenge.CreateChallengeScreen
import de.cleema.android.joinedchallenges.JoinedChallengeScreen
import de.cleema.android.partnerchallenges.DetailChallengeScreen
import java.util.*

const val ChallengesGraphRoutePattern = "challenges"
internal const val ChallengesListRoutePattern = "$ChallengesGraphRoutePattern/list"
internal const val CreateChallengeRoutePattern = "$ChallengesGraphRoutePattern/create"
private const val challengeIdArg = "challengeId"

fun NavGraphBuilder.challengesGraph(navController: NavController) {
    navigation(
        startDestination = ChallengesListRoutePattern,
        route = ChallengesGraphRoutePattern
    ) {
        challengesScreen(
            onNavigationToChallenge = navController::navigateToPartnerChallenge,
            onNavigationToJoinedChallenge = navController::navigateToJoinedChallenge,
            onNavigationToCreateChallenge = navController::navigateToCreateChallenge
        )

        partnerChallengeScreen()

        joinedChallengeScreen(navController)

        createChallenge { navController.popBackStack() }
    }
}

fun NavGraphBuilder.challengesScreen(
    onNavigationToChallenge: (partnerChallenge: UUID) -> Unit,
    onNavigationToJoinedChallenge: (joinedChallenge: UUID) -> Unit,
    onNavigationToCreateChallenge: () -> Unit
) {
    composable(ChallengesListRoutePattern) {
        ChallengesScreen(
            modifier = Modifier.fillMaxSize(),
            onNavigationToChallenge = onNavigationToChallenge,
            onNavigationToJoinedChallenge = onNavigationToJoinedChallenge,
            onCreateChallengeClicked = onNavigationToCreateChallenge
        )
    }
}

fun NavGraphBuilder.partnerChallengeScreen() {
    composable(
        "$ChallengesGraphRoutePattern/{$challengeIdArg}",
        arguments = listOf(
            navArgument(challengeIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        DetailChallengeScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}

fun NavGraphBuilder.joinedChallengeScreen(
    navController: NavController,
    baseRoute: String = "$ChallengesGraphRoutePattern/joined"
) {
    composable(
        "$baseRoute/{$challengeIdArg}",
        arguments = listOf(
            navArgument(challengeIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        JoinedChallengeScreen(
            modifier = Modifier.fillMaxSize(),
            onNotFound = navController::popBackStack
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun NavGraphBuilder.createChallenge(onDismiss: () -> Unit) {
    dialog(
        CreateChallengeRoutePattern,
        dialogProperties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        CreateChallengeScreen(
            modifier = Modifier.fillMaxSize(),
            onClose = onDismiss
        )
    }
}

fun NavController.navigateToPartnerChallenge(challengeId: UUID) {
    this.navigate("$ChallengesGraphRoutePattern/$challengeId")
}

private fun NavController.navigateToJoinedChallenge(challengeId: UUID) {
    this.navigate("$ChallengesGraphRoutePattern/joined/$challengeId")
}

fun NavController.navigateToCreateChallenge() {
    this.navigate(CreateChallengeRoutePattern)
}

