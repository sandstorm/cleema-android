/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.data.network.responses.ImageResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class OfferResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val title: String,
    val summary: String,
    val description: String,
    val image: ImageResponse?,
    val validFrom: Instant,
    val region: RegionResponse?,
    val location: LocationResponse?,
    val address: AddressResponse?,
    val discount: Int,
    val storeType: StoreType,
    val voucherRedeem: VoucherRedemptionResponse,
    val genericVoucher: String?,
    val websiteUrl: String?
) {
    @Serializable
    enum class StoreType {
        shop,
        online
    }
}

@Serializable
data class AddressResponse(
    val street: String?,
    val city: String?,
    val zip: String?,
    val housenumber: String?
)

@Serializable
data class VoucherRedemptionResponse(
    val redeemAvailableDate: Instant?,
    val redeemedCode: String?,
    val redeemAvailable: Boolean,
    val vouchersExhausted: Boolean
)
