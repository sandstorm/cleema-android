package de.cleema.android.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun <T> SameHeightLazyGrid(
    columnsCount: Int,
    content: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    item: @Composable RowScope.(item: T, modifier: Modifier) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.Start
    ) {
        val itemsCount = content.count()
        items((itemsCount + 1) / columnsCount) { i ->
            Row(Modifier.height(IntrinsicSize.Max), horizontalArrangement) {
                for (j in 0 until columnsCount) {
                    val index = i * columnsCount + j
                    if (index < itemsCount) {
                        item(
                            content[index], modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SameHeightPreview() {
    SameHeightLazyGrid(columnsCount = 2, content = (1..100).toList()) { item, modifier ->
        Text(
            LoremIpsum(Random.nextInt(1, 10)).values.first(),
            modifier = modifier
                .background(
                    when (Random.nextInt(1, 10) % 3) {
                        0 -> Color.LightGray
                        1 -> Color.DarkGray
                        else -> Color.Gray
                    }
                )
        )
    }
}
