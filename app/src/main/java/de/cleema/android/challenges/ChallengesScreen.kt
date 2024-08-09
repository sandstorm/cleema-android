package de.cleema.android.challenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.ContentBackground
import de.cleema.android.joinedchallenges.JoinedChallengeList
import de.cleema.android.partnerchallenges.PartnerChallenges
import java.util.*

@Composable
fun ChallengesScreen(
    modifier: Modifier = Modifier,
    onNavigationToChallenge: (partnerChallenge: UUID) -> Unit,
    onNavigationToJoinedChallenge: (joinedChallenge: UUID) -> Unit,
    onCreateChallengeClicked: () -> Unit
) {
    Box(modifier = modifier) {
        ContentBackground(modifier = Modifier.matchParentSize())

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            JoinedChallengeList(
                showsEmptyPlaceholder = true,
                onClick = onNavigationToJoinedChallenge,
                onCreateClick = onCreateChallengeClicked
            )

            PartnerChallenges(onChallengeClick = onNavigationToChallenge)
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp, bottom = 16.dp),
            onClick = onCreateChallengeClicked,
            shape = CircleShape,
            containerColor = Action,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, stringResource(R.string.create_challenge_floating_button))
        }
    }
}
