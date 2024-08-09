/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun OfferRedemptionPendingView(
    modifier: Modifier = Modifier,
    discount: Int = 0,
    onRedeemClicked: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (discount > 0) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(stringResource(id = R.string.marketplace_voucher_percentage_format_string, discount))
                    }

                    append(" ")
                    withStyle(
                        style = SpanStyle(
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontStyle = MaterialTheme.typography.bodySmall.fontStyle
                        )
                    ) {
                        append(stringResource(R.string.market_item_discount))
                    }
                }
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onRedeemClicked,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Action
            ),
            modifier = Modifier.alignBy(LastBaseline)
        ) {
            Text(text = stringResource(R.string.request_voucher), color = Color.White)
        }
    }
}


@Preview(widthDp = 360, heightDp = 50, name = "With discount", showBackground = true)
@Composable
fun OfferRedemptionPendingViewPreview() {
    CleemaTheme {
        OfferRedemptionPendingView(discount = 42) {}
    }
}

@Preview(widthDp = 360, heightDp = 50, name = "Without discount", showBackground = true)
@Composable
fun OfferRedemptionPendingViewPreviewNoDiscount() {
    CleemaTheme {
        OfferRedemptionPendingView(discount = 0) {}
    }
}
