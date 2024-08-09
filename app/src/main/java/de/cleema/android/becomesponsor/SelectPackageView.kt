/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SelectPackageView(
    packages: List<de.cleema.android.core.models.SponsorPackage>,
    selectedPackage: de.cleema.android.core.models.SponsorPackage,
    modifier: Modifier = Modifier,
    onSelectPackage: (de.cleema.android.core.models.SponsorPackage) -> Unit
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
            SponsorItem(content = packages[page])
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

        de.cleema.android.core.components.MarkdownText(
            markdown = selectedPackage.markdown,
            config = de.cleema.android.core.components.MarkdownTextConfiguration(scrollToTop = true)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun SelectPackageViewPreview() {
    CleemaTheme {
        SelectPackageView(
            packages = de.cleema.android.core.models.SponsorPackage.all,
            selectedPackage = de.cleema.android.core.models.SponsorPackage.maker,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onSelectPackage = {}
        )
    }
}
