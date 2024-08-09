package de.cleema.android.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import de.cleema.android.challenges.joinedChallengeScreen
import de.cleema.android.projects.projectScreen
import de.cleema.android.quiz.AnswerQuiz
import de.cleema.android.social.InviteUsersByLink
import java.util.*

const val DashboardGraphRoutePattern = "dashboard"
const val DashboardGridRoutePattern = "$DashboardGraphRoutePattern/grid"
const val DashboardAnswerQuizRoutePattern = "$DashboardGraphRoutePattern/quiz"
const val DashboardSocialInvitePattern = "$DashboardGraphRoutePattern/invite"

fun NavGraphBuilder.dashboardGraph(navController: NavController) {
    navigation(
        startDestination = DashboardGridRoutePattern,
        route = DashboardGraphRoutePattern
    ) {
        dashboardScreen(
            {
                navController.navigateToQuiz()
            },
            onNavigateToChallenge = {
                navController.navigateToJoinedChallenge(it)
            },
            onNavigateToProject = navController::navigateToProject,
            onNavigateToSocial = navController::navigateToShareSocial
        )

        answerQuiz()
        shareSocial {
            navController.popBackStack()
        }

        projectScreen("$DashboardGraphRoutePattern/project")
        joinedChallengeScreen(navController, "$DashboardGraphRoutePattern/joined")
    }
}

private fun NavGraphBuilder.shareSocial(onDismiss: () -> Unit) {
    dialog(DashboardSocialInvitePattern) {
        InviteUsersByLink(
            modifier = Modifier.wrapContentHeight(), onDismissClicked = onDismiss
        )
    }
}

internal fun NavGraphBuilder.dashboardScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateToChallenge: (UUID) -> Unit,
    onNavigateToProject: (UUID) -> Unit,
    onNavigateToSocial: () -> Unit,
) {
    composable(DashboardGridRoutePattern) {
        DashboardScreen(
            modifier = Modifier.fillMaxSize(),
            onNavigateToQuiz = onNavigateToQuiz,
            onNavigateToChallenge = onNavigateToChallenge,
            onNavigateToProject = onNavigateToProject,
            onNavigateToSocial = onNavigateToSocial
        )
    }
}

internal fun NavGraphBuilder.answerQuiz() {
    composable(DashboardAnswerQuizRoutePattern) {
        AnswerQuiz()
    }
}

fun NavController.navigateToQuiz() {
    this.navigate(DashboardAnswerQuizRoutePattern)
}

fun NavController.navigateToShareSocial() {
    this.navigate(DashboardSocialInvitePattern)
}

private fun NavController.navigateToProject(projectId: UUID) {
    this.navigate("$DashboardGraphRoutePattern/project/$projectId")
}

private fun NavController.navigateToJoinedChallenge(challengeId: UUID) {
    this.navigate("$DashboardGraphRoutePattern/joined/$challengeId")
}
