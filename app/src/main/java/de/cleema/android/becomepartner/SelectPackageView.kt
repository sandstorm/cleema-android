/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomepartner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import de.cleema.android.R
import de.cleema.android.core.components.MarkdownText
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SelectPackageView(
    packages: List<de.cleema.android.core.models.PartnerPackage>,
    selectedPackage: de.cleema.android.core.models.PartnerPackage,
    modifier: Modifier = Modifier,
    onSelectPackage: (de.cleema.android.core.models.PartnerPackage) -> Unit,
    onContactClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(stringResource(R.string.select_package_headline), style = MaterialTheme.typography.titleMedium)
        val pagerState = rememberPagerState(packages.indexOf(selectedPackage))

        HorizontalPager(
            count = packages.count(),
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
        ) { page ->
            PartnerItem(content = packages[page])
        }

        LaunchedEffect(key1 = pagerState.currentPage) {
            if (packages[pagerState.currentPage].type != selectedPackage.type) {
                onSelectPackage(packages[pagerState.currentPage])
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = DefaultText,
            inactiveColor = Accent,
            indicatorWidth = 10.dp,
            spacing = 8.dp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        MarkdownText(markdown = selectedPackage.features)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContactClick,
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = Action, contentColor = Color.White)
        ) {
            Text(stringResource(R.string.partnership_contact_button), color = Color.White)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun SelectPackageViewPreview() {
    CleemaTheme {
        SelectPackageView(
            packages = de.cleema.android.core.models.PartnerPackage.all,
            selectedPackage = de.cleema.android.core.models.PartnerPackage.darlings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onSelectPackage = {},
            onContactClick = {}
        )
    }
}
