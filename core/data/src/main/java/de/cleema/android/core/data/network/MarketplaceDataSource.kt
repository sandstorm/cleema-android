/*
 * Created by Kumpels and Friends on 2022-12-05
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.OfferResponse
import java.util.*

interface MarketplaceDataSource {
    suspend fun getOffers(regionId: UUID?): Result<List<OfferResponse>>
    suspend fun getOffer(offerId: UUID): Result<OfferResponse>
    suspend fun redeem(offerId: UUID): Result<OfferResponse>
}
