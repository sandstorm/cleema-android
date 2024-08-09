/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.becomesponsor.BecomeSponsorUiState.*
import de.cleema.android.core.models.SponsorData
import de.cleema.android.core.models.SponsorPackage

@Composable
fun BecomeSponsorScreen(
    modifier: Modifier = Modifier,
    viewModel: BecomeSponsorViewModel = hiltViewModel(),
    onCloseClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BecomeSponsorScreen(
        uiState,
        modifier.padding(20.dp),
        onNextClick = {
            viewModel.onNextClick()
            if (uiState is Thanks) {
                onCloseClick()
            }
        },
        onSelectPackage = viewModel::onSelectPackage,
        onEditSponsorData = viewModel::onEditSponsorData,
        onSepaEnabledClick = viewModel::onConfirmSepaClick
    )
}

@Composable
fun BecomeSponsorScreen(
    uiState: BecomeSponsorUiState,
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
    onSelectPackage: (SponsorPackage) -> Unit,
    onEditSponsorData: (SponsorData) -> Unit,
    onSepaEnabledClick: () -> Unit,
) {
    BecomeSponsorStepScreen(
        nextButtonEnabled = uiState.isNextButtonEnabled,
        modifier = modifier,
        buttonTitle = uiState::buttonTitle,
        onNextClick = onNextClick
    ) {
        when (uiState) {
            is Error -> de.cleema.android.core.components.ErrorScreen(message = uiState.reason)
            Loading -> de.cleema.android.core.components.LoadingScreen()
            is Confirm -> ConfirmDataView(
                selectedPackage = uiState.selectedPackage,
                sponsorData = uiState.sponsorData,
                uiState.sepaConfirmed,
                modifier = Modifier.weight(1f),
                onChangePackageClick = { onSelectPackage(uiState.selectedPackage) },
                onEditClick = { onEditSponsorData(uiState.sponsorData) },
                onSepaEnabledClick = onSepaEnabledClick
            )
            is EnterData -> EnterDataView(
                sponsorData = uiState.sponsorData,
                validationError = uiState.validationError,
                modifier = Modifier.weight(1f),
                onEditSponsorData = onEditSponsorData
            )
            is SelectPackage -> SelectPackageView(
                packages = uiState.packages,
                selectedPackage = uiState.selectedPackage,
                modifier = Modifier.weight(1f),
                onSelectPackage = onSelectPackage
            )
            Thanks -> ThanksScreen()
        }
    }
}

fun BecomeSponsorUiState.buttonTitle(): Int? {
    return when (this) {
        is EnterData, is SelectPackage -> R.string.become_sponsor_next_button_title
        is Confirm -> R.string.become_sponsor_confirm_button_title
        Thanks -> R.string.become_sponsor_close_button_title
        else -> null
    }
}
