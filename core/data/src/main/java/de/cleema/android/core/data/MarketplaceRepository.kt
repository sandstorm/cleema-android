/*
 * Created by Kumpels and Friends on 2022-12-05
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.models.MarketItem
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MarketplaceRepository {
    fun getMarketplaceStream(regionId: UUID): Flow<Result<List<MarketItem>>>
    fun getOfferStream(offerId: UUID): Flow<Result<MarketItem>>

    fun getVoucherStream(offerId: UUID): Flow<String?>
    suspend fun requestVoucher(offerId: UUID)

    suspend fun redeemVoucher(offerId: UUID, code: String)
}
