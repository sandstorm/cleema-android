/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import de.cleema.android.R
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.CleemaTheme
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.RedeemVoucher


@Composable
fun OfferRedemptionCodeView(
    code: String,
    modifier: Modifier = Modifier,
    discount: Int = 0,
    isIndividual: Boolean = false,
    // onRedeemClick: (() -> Unit)? = null,
    website: String?
) {
    // TODO: simplify
    Box(modifier = modifier) {
        Column(Modifier.height(254.dp)) {
            Spacer(Modifier.weight(1f))

            Surface(
                shape = MaterialTheme.shapes.medium, color = Color.White, shadowElevation = 19.dp, modifier = Modifier
                    .height(165.dp)
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .background(RedeemVoucher)
                            .fillMaxHeight()
                            .weight(0.5f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (discount > 0) {
                            Column(
                                horizontalAlignment = Alignment.Start, modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    stringResource(R.string.marketplace_voucher_save_title),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.White
                                )

                                Text(
                                    text = stringResource(
                                        R.string.marketplace_voucher_percentage_format_string,
                                        discount
                                    ),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Box(
                        Modifier
                            .background(Color.White)
                            .fillMaxHeight()
                            .weight(0.5f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End, modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            if (isIndividual) {
                                Text(
                                    stringResource(R.string.marketplace_voucher_one_time_use),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.LightGray
                                )
                            } else {
                                Text(
                                    stringResource(R.string.marketplace_voucher_title),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.LightGray
                                )
                            }

                            Text(code, style = MaterialTheme.typography.headlineSmall, color = DefaultText)



//                            if (onRedeemClick != null) {
//                                onRedeemClick.let { action ->
//                                    Spacer(Modifier.weight(1f))
//
//                                    Button(
//                                        onClick = action,
//                                        modifier = Modifier
//                                            .fillMaxWidth(),
//                                        shape = MaterialTheme.shapes.medium,
//                                        colors = ButtonDefaults.buttonColors(
//                                            containerColor = Action,
//                                            contentColor = Color.White
//                                        )
//                                    ) {
//                                        Text(stringResource(R.string.market_item_redeem), color = Color.White)
//                                    }
//                                }
//                            }
                                Button(
                                    onClick = copyCodeToClipboard(code),
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Action,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(stringResource(R.string.market_item_copy_link), color = Color.White)
                                }

                                if (website != null) {
                                    Button(
                                        onClick = openWebsiteLink(website),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Action,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text(stringResource(R.string.market_item_visit_website), color = Color.White)
                                    }
                                }

                        }
                    }
                }
            }
        }
        Row {
            Image(painterResource(R.drawable.voucher), contentDescription = null, Modifier.weight(0.5f))
            Spacer(Modifier.weight(0.5f))
        }
    }
}

@Composable
fun copyCodeToClipboard(coupon: String): () -> Unit {
    val context = LocalContext.current
    val clipboard = getSystemService(context, ClipboardManager::class.java)

    // This works somehow, dont know why
    return {
        if (clipboard != null) {
            val clip = ClipData.newPlainText("label", coupon)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Clipboard service not available", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun openWebsiteLink(url: String): () -> Unit {
    val context = LocalContext.current

    val usableWebsiteUrl = if(!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url

    return {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(usableWebsiteUrl))
            startActivity(context, browserIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }
}

@Preview(widthDp = 360, heightDp = 300, name = "With discount", showBackground = false)
@Composable
fun OfferRedemptionCodeViewPreview() {
    CleemaTheme {
        OfferRedemptionCodeView(
            code = "voucher1",
            Modifier.padding(20.dp),
            discount = 22,
            // onRedeemClick = {},
            website = null
        )
    }
}

@Preview(widthDp = 360, heightDp = 300, name = "Without discount", showBackground = false)
@Composable
fun OfferRedemptionCodeViewPreviewWithoutDiscount() {
    CleemaTheme {
        OfferRedemptionCodeView(
            code = "voucher1",
            Modifier.padding(20.dp),
            discount = 0,
            // onRedeemClick = {},
            website = null
        )
    }
}
