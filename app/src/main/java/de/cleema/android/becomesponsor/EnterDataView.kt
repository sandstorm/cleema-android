/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.becomesponsor.BecomeSponsorUiState.EnterData.ValidationError
import de.cleema.android.becomesponsor.BecomeSponsorUiState.EnterData.ValidationError.*
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterDataView(
    sponsorData: de.cleema.android.core.models.SponsorData,
    validationError: ValidationError?,
    modifier: Modifier = Modifier,
    onEditSponsorData: (de.cleema.android.core.models.SponsorData) -> Unit
) {
    val textFieldModifier = Modifier.fillMaxWidth()
    val textFieldColors = TextFieldDefaults.textFieldColors(
        containerColor = White, focusedIndicatorColor = Action,
        unfocusedIndicatorColor = LightGray,
    )
    val bankInfoKeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Ascii,
        autoCorrect = false,
        capitalization = KeyboardCapitalization.Characters
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .background(White),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(stringResource(R.string.become_sponsor_enter_data_headline), style = MaterialTheme.typography.titleMedium)

        TextField(
            value = sponsorData.firstName,
            modifier = textFieldModifier,
            label = { Text(stringResource(R.string.become_sponsor_enter_data_firstname)) },
            colors = textFieldColors,
            singleLine = true,
            onValueChange = {
                onEditSponsorData(sponsorData.copy(firstName = it))
            },
        )
        TextField(
            value = sponsorData.lastName,
            modifier = textFieldModifier,
            label = { Text(stringResource(R.string.become_sponsor_enter_data_lastname)) },
            colors = textFieldColors,
            onValueChange = {
                onEditSponsorData(sponsorData.copy(lastName = it))
            })
        TextField(
            value = sponsorData.streetAndHouseNumber,
            modifier = textFieldModifier,
            label = { Text(stringResource(R.string.become_sponsor_enter_data_street)) },
            colors = textFieldColors,
            singleLine = true,
            onValueChange = {
                onEditSponsorData(sponsorData.copy(streetAndHouseNumber = it))
            })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = textFieldModifier) {
            TextField(
                value = sponsorData.postalCode,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.become_sponsor_enter_data_postcode)) },
                colors = textFieldColors,
                singleLine = true,
                onValueChange = {
                    onEditSponsorData(sponsorData.copy(postalCode = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrect = false)
            )
            TextField(
                value = sponsorData.city,
                modifier = Modifier.weight(2f),
                label = { Text(stringResource(R.string.become_sponsor_enter_data_city)) },
                colors = textFieldColors,
                singleLine = true,
                onValueChange = {
                    onEditSponsorData(sponsorData.copy(city = it))
                })
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = textFieldModifier
        ) {
            TextField(
                value = sponsorData.iban,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(R.string.become_sponsor_enter_data_iban)) },
                colors = textFieldColors,
                singleLine = true,
                maxLines = 1,
                isError = validationError == IBAN_INVALID,
                onValueChange = {
                    onEditSponsorData(sponsorData.copy(iban = it))
                },
                keyboardOptions = bankInfoKeyboardOptions
            )

            validationError?.let {
                if (it == IBAN_INVALID) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_error_24),
                        contentDescription = stringResource(R.string.become_sponsor_enter_data_validation_error_ax_description),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        TextField(
            value = sponsorData.bic,
            modifier = textFieldModifier,
            label = { Text(stringResource(R.string.become_sponsor_enter_data_bic)) },
            colors = textFieldColors,
            singleLine = true,
            onValueChange = {
                onEditSponsorData(sponsorData.copy(bic = it))
            },
            keyboardOptions = bankInfoKeyboardOptions
        )

        Text(
            stringResource(R.string.become_sponsor_enter_data_confirmation),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        de.cleema.android.core.components.LinkButton(
            title = stringResource(R.string.become_sponsor_privacy_link_title),
            uriString = stringResource(R.string.become_sponsor_privacy_url),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun EnterDataViewPreview() {
    var data by remember {
        mutableStateOf(de.cleema.android.core.models.SponsorData.DEMO)
    }
    CleemaTheme {
        EnterDataView(
            sponsorData = data,
            validationError = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onEditSponsorData = { data = it }
        )
    }
}
