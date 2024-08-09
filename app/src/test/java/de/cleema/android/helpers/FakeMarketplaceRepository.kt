/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.MarketplaceRepository
import de.cleema.android.core.models.MarketItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*

class FakeMarketplaceRepository : MarketplaceRepository {
    var redeemedId: UUID? = null
    var stubbedItems = Channel<Result<List<MarketItem>>>()
    var stubbedOffers = Channel<Result<MarketItem>>()
    var stubbedVouchers = Channel<String?>()
    var redeemed: Pair<UUID, String>? = null

    override fun getMarketplaceStream(regionId: UUID): Flow<Result<List<MarketItem>>> = stubbedItems.receiveAsFlow()

    override fun getOfferStream(offerId: UUID): Flow<Result<MarketItem>> = stubbedOffers.receiveAsFlow()

    override suspend fun requestVoucher(offerId: UUID) {
        redeemedId = offerId
    }

    override fun getVoucherStream(offerId: UUID): Flow<String?> = stubbedVouchers.receiveAsFlow()

    override suspend fun redeemVoucher(offerId: UUID, code: String) {
        redeemed = offerId to code
    }
}
