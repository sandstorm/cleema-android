/*
 * Created by Kumpels and Friends on 2023-01-17
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.becomesponsor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.components.LinkButton
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.Answer
import de.cleema.android.core.styling.CleemaTheme

@Composable
fun ConfirmDataView(
    selectedPackage: de.cleema.android.core.models.SponsorPackage,
    sponsorData: de.cleema.android.core.models.SponsorData,
    sepaEnabled: Boolean,
    modifier: Modifier = Modifier,
    onChangePackageClick: () -> Unit,
    onEditClick: () -> Unit,
    onSepaEnabledClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.become_sponsor_confirm_headline), style = MaterialTheme.typography.titleMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        stringResource(R.string.become_sponsor_confirm_selected_package),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        stringResource(R.string.become_sponsor_confirm_package_title, selectedPackage.title),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    ClickableText(
                        text = AnnotatedString(
                            stringResource(R.string.become_sponsor_confirm_change), SpanStyle(
                                fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                                color = Action
                            )
                        ),
                        onClick = { onChangePackageClick() }, modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        stringResource(R.string.become_sponsor_confirm_monthly_price),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        stringResource(R.string.become_sponsor_confirm_price, selectedPackage.price),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Answer
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        stringResource(R.string.become_sponsor_confirm_my_data),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(sponsorData.fullName, style = MaterialTheme.typography.bodyMedium)
                    Text(sponsorData.address, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        stringResource(R.string.become_sponsor_confirm_iban, sponsorData.iban),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (sponsorData.bic.isNotBlank()) {
                        Text(
                            stringResource(R.string.become_sponsor_confirm_bic, sponsorData.bic),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    LinkButton(
                        title = stringResource(R.string.become_sponsor_privacy_link_title),
                        uriString = stringResource(R.string.become_sponsor_privacy_url),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    ClickableText(
                        text = AnnotatedString(
                            stringResource(R.string.become_sponsor_confirm_change), SpanStyle(
                                fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
                                color = Action
                            )
                        ),
                        onClick = { onEditClick() }, modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = sepaEnabled,
                        onCheckedChange = { onSepaEnabledClick() }
                    )
                    Text(
                        text = stringResource(R.string.become_sponsor_confirm_sepa_title),
                        modifier = Modifier.clickable { onSepaEnabledClick() },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(id = R.string.become_sponsor_confirm_sepa_body),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        stringResource(R.string.become_sponsor_confirm_features),
                        style = MaterialTheme.typography.titleSmall
                    )
                    de.cleema.android.core.components.MarkdownText(
                        selectedPackage.markdown,
                        style = MaterialTheme.typography.bodyMedium,
                        config = de.cleema.android.core.components.MarkdownTextConfiguration(scrollToTop = true)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun ConfirmDataViewPreview() {
    CleemaTheme {
        ConfirmDataView(
            selectedPackage = de.cleema.android.core.models.SponsorPackage.fan,
            sponsorData = de.cleema.android.core.models.SponsorData.DEMO,
            false,
            modifier = Modifier.padding(),
            onChangePackageClick = {},
            onEditClick = {},
            onSepaEnabledClick = {}
        )
    }
}
