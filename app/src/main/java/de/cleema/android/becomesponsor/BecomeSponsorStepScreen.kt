/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun BecomeSponsorStepScreen(
    nextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
    buttonTitle: () -> Int? = { R.string.become_sponsor_next_button_title },
    content: @Composable (ColumnScope.() -> Unit)
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            content()

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Action, contentColor = Color.White),
                enabled = nextButtonEnabled
            ) {
                buttonTitle()?.let {
                    Text(stringResource(id = it), color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF59E5BF, showSystemUi = true)
@Composable
fun BecomeSponsorStepScreenPreview() {
    CleemaTheme {
        BecomeSponsorStepScreen(
            nextButtonEnabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onNextClick = {},
        ) {
            Text("Content here")
        }
    }
}
