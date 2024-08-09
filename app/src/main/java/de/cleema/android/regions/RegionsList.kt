/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.regions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import java.util.*


@Composable
fun RegionsList(
    modifier: Modifier = Modifier,
    viewModel: RegionsViewModel = hiltViewModel(),
    onSelectRegion: (de.cleema.android.core.models.Region) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is RegionsUiState.Success -> RegionsList(
            modifier = modifier,
            selectedRegion = state.selection,
            regions = state.regions,
            onSelectionChange = {
                viewModel.select(it.id)
                onSelectRegion(it)
            })
        is RegionsUiState.Loading -> CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.width(16.dp)
        )
        else -> Text(stringResource(R.string.regions_error))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionsList(
    modifier: Modifier = Modifier,
    regions: List<de.cleema.android.core.models.Region> = listOf(),
    selectedRegion: de.cleema.android.core.models.Region? = null,
    onSelectionChange: (de.cleema.android.core.models.Region) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        modifier = modifier,
        onExpandedChange = {
            expanded = !expanded
        },
    ) {
        TextField(
            readOnly = true,
            value = selectedRegion?.name
                ?: stringResource(R.string.no_region_selection_placeholder),
            onValueChange = { },
            label = { Text(stringResource(R.string.select_region_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                containerColor = Color.White,
                focusedIndicatorColor = Action,
                unfocusedIndicatorColor = Color.LightGray,
                focusedTrailingIconColor = Action,
                unfocusedTrailingIconColor = Action,
            )
        )
        DropdownMenu(
            expanded = expanded,
            modifier = Modifier
                .exposedDropdownSize()
                .background(Color.White),
            onDismissRequest = {
                expanded = false
            },
        ) {
            regions.forEach { region ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onSelectionChange(region)
                        expanded = false
                    },
                    text = {
                        Text(text = region.name)
                    }
                )
            }
        }
    }
}

@Preview(name = "Region picker", widthDp = 320, heightDp = 400)
@Composable
fun RegionsListPreview(
    regions: List<de.cleema.android.core.models.Region> = listOf(
        de.cleema.android.core.models.Region(name = "Leipzig"),
        de.cleema.android.core.models.Region(name = "Dresden")
    )
) {
    var selectedRegion by remember { mutableStateOf(regions[0]) }
    CleemaTheme {
        RegionsList(
            modifier = Modifier.fillMaxWidth(),
            selectedRegion = selectedRegion,
            regions = regions,
            onSelectionChange = { selectedRegion = it }
        )
    }

}
