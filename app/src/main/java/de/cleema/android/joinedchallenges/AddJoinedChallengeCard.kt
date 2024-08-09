package de.cleema.android.joinedchallenges

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.Light
import de.cleema.android.core.styling.TextInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJoinedChallengeCard(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .aspectRatio(1f, false),
        onClick = onTap,
        colors = CardDefaults.cardColors(
            containerColor = Light
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.add_challenge_empty_placeholder),
                style = MaterialTheme.typography.titleMedium,
                color = TextInput,
                modifier = Modifier.align(Alignment.TopStart)
            )

            IconButton(onClick = onTap, colors = IconButtonDefaults.iconButtonColors(contentColor = Action)) {
                Icon(
                    painter = painterResource(id = R.drawable.plus_circle),
                    contentDescription = stringResource(R.string.add_challenge_empty_placeholder)
                )
            }
        }
    }
}

@Preview(name = "Add joined challenge", widthDp = 160)
@Composable
fun AddJoinedChallengeCardPreview() {
    CleemaTheme {
        AddJoinedChallengeCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        )
    }
}
