/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomepartner

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun BecomePartnerScreen(
    modifier: Modifier = Modifier,
    viewModel: BecomePartnerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BecomePartnerScreen(
        uiState,
        modifier.padding(20.dp),
        onSelectPackage = viewModel::onSelectPackage,
        onContactClick = viewModel::onContactClick
    )
}

@Composable
fun BecomePartnerScreen(
    uiState: BecomePartnerUiState,
    modifier: Modifier = Modifier,
    onSelectPackage: (de.cleema.android.core.models.PartnerPackage) -> Unit,
    onContactClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = MaterialTheme.shapes.medium,
        contentColor = Color.White
    ) {
        when (uiState) {
            is BecomePartnerUiState.Error -> de.cleema.android.core.components.ErrorScreen(message = uiState.reason)
            BecomePartnerUiState.Loading -> de.cleema.android.core.components.LoadingScreen()
            is BecomePartnerUiState.SelectPackage -> SelectPackageView(
                packages = uiState.packages,
                selectedPackage = uiState.selectedPackage,
                modifier = Modifier.padding(20.dp),
                onSelectPackage = onSelectPackage,
                onContactClick = onContactClick
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF59E5BF, showSystemUi = true)
@Composable
fun BecomePartnerScreenPreview() {
    CleemaTheme {
        BecomePartnerScreen(
            BecomePartnerUiState.SelectPackage(
                packages = de.cleema.android.core.models.PartnerPackage.all,
                selectedPackage = de.cleema.android.core.models.PartnerPackage.starter
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onSelectPackage = {},
            onContactClick = {}
        )
    }
}
