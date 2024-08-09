/*
 * Created by Kumpels and Friends on 2022-12-16
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.projects

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.composable
import de.cleema.android.projects.details.ProjectDetailContent
import de.cleema.android.projects.details.ProjectDetailScreen
import de.cleema.android.projects.list.ProjectsScreen
import java.util.*

const val ProjectsGraphRoutePattern = "projects"
internal const val ProjectsListRoutePattern = "$ProjectsGraphRoutePattern/list"
private const val projectIdArg = "projectId"

fun NavGraphBuilder.projectsGraph(navController: NavController) {
    navigation(
        startDestination = ProjectsListRoutePattern,
        route = ProjectsGraphRoutePattern
    ) {
        projectsScreen(
            onNavigationToProject = { id ->
                navController.navigateToProject(id)
            }
        )

        projectScreen()
    }
}

fun NavGraphBuilder.projectsScreen(
    onNavigationToProject: (projectId: UUID) -> Unit
) {
    composable(ProjectsListRoutePattern) {
        ProjectsScreen(
            modifier = Modifier.fillMaxSize(),
            onProjectClick = { onNavigationToProject(it) }
        )
    }
}

fun NavGraphBuilder.projectScreen(baseRoute: String = ProjectsGraphRoutePattern, showBackground: Boolean = true) {
    composable(
        "$baseRoute/{$projectIdArg}",
        arguments = listOf(
            navArgument(projectIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        if (showBackground) {
            ProjectDetailScreen()
        } else {
            ProjectDetailContent()
        }
    }
}

private fun NavController.navigateToProject(projectId: UUID) {
    this.navigate("$ProjectsGraphRoutePattern/$projectId")
}
