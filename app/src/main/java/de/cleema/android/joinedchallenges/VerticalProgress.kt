package de.cleema.android.joinedchallenges

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import de.cleema.android.core.styling.Dimmed

@Composable
fun VerticalProgress(
    modifier: Modifier = Modifier,
    progress: Double,
    radius: Dp = 20.dp,
    width: Dp = 6.dp
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        val (indicator, capsule, value) = createRefs()

        Image(
            modifier = Modifier.constrainAs(indicator) {
                end.linkTo(capsule.start, margin = 4.dp)
                centerVerticallyTo(parent, 1 - progress.toFloat())
            },
            painter = painterResource(id = de.cleema.android.R.drawable.arrow_right),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Dimmed)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radius))
                .background(Color.LightGray)
                .width(width)
                .fillMaxHeight()
                .constrainAs(capsule) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Box(
            modifier = Modifier
                .width(width)
                .clip(RoundedCornerShape(radius))
                .background(Dimmed)
                .constrainAs(value) {
                    centerHorizontallyTo(capsule)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(progress.toFloat())
        )
    }

}

@Preview("Verical progress", widthDp = 36)
@Composable
fun VerticalProgressPreview() {
    VerticalProgress(
        modifier = Modifier.fillMaxWidth(),
        progress = 0.3
    )
}
