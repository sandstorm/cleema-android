/*
 * Created by Kumpels and Friends on 2022-12-08
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import de.cleema.android.core.common.BroadCaster
import de.cleema.android.core.data.network.MarketplaceDataSource
import de.cleema.android.core.data.network.responses.AddressResponse
import de.cleema.android.core.data.network.responses.OfferResponse
import de.cleema.android.core.data.network.responses.VoucherRedemptionResponse
import de.cleema.android.core.models.Address
import de.cleema.android.core.models.MarketItem
import de.cleema.android.core.models.MarketItem.StoreType.Online
import de.cleema.android.core.models.MarketItem.StoreType.Shop
import de.cleema.android.core.models.MarketItem.VoucherRedemption.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.net.URL
import java.util.*

class DefaultMarketplaceRepository(
    private val dataSource: MarketplaceDataSource,
    private val baseURL: URL,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val context: Context
) : MarketplaceRepository {
    private val sharedPreferences by lazy {
        val mainKey: MasterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "vouchers",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    private var _vouchersFlow: MutableStateFlow<Map<String, String>> =
        MutableStateFlow(
            kotlin.runCatching {
                sharedPreferences.getSerializable<Map<String, String>>(PreferenceKeys.VOUCHERS_KEY.name) ?: mapOf()
            }.getOrDefault(mapOf())
        )
    private val broadCaster = BroadCaster<UUID>()
    override fun getMarketplaceStream(regionId: UUID): Flow<Result<List<MarketItem>>> = flow {
        emit(dataSource.getOffers(regionId).map {
            it.map { response ->
                response.toMarketItem(baseURL)
            }
        })
    }.flowOn(ioDispatcher)

    override fun getOfferStream(offerId: UUID): Flow<Result<MarketItem>> = flow {
        emit(dataSource.getOffer(offerId).map { it.toMarketItem(baseURL) })

        broadCaster.events.filter { it == offerId }.map {
            dataSource.getOffer(offerId).map { it.toMarketItem(baseURL) }
        }
            .collect(this)
    }.flowOn(ioDispatcher)

    override suspend fun requestVoucher(offerId: UUID) {
        dataSource.redeem(offerId).onSuccess {
            broadCaster.post(offerId)
        }
    }

    override fun getVoucherStream(offerId: UUID): Flow<String?> = _vouchersFlow.map {
        it.getOrDefault(offerId.toString(), null)
    }

    override suspend fun redeemVoucher(offerId: UUID, code: String) {
        with(sharedPreferences.edit()) {
            val updated =
                (sharedPreferences.getSerializable<Map<String, String>?>(PreferenceKeys.VOUCHERS_KEY.name)
                    ?: mapOf()).toMutableMap().also {
                    it[offerId.toString()] = code
                }
            kotlin.runCatching {
                putSerializable(PreferenceKeys.VOUCHERS_KEY.name, updated)
            }.onSuccess {
                this.apply()
                _vouchersFlow.value = updated
            }
        }
    }
}

private fun OfferResponse.toMarketItem(baseURL: URL): MarketItem = MarketItem(
    uuid,
    title,
    summary,
    description,
    discount,
    image?.toImage(baseURL),
    region?.toRegion(),
    location?.toLocation(),
    storeType = storeType.toMarketItemStoreType(),
    voucherRedemption = voucherRedeem.toVoucherRedemption(genericVoucher),
    address = address?.toAddress(),
    websiteUrl
)

private fun AddressResponse.toAddress(): Address = Address(
    street = street,
    city = city,
    zipcode = zip,
    houseNumber = housenumber
)

private fun OfferResponse.StoreType.toMarketItemStoreType(): MarketItem.StoreType =
    when (this) {
        OfferResponse.StoreType.online -> Online
        OfferResponse.StoreType.shop -> Shop
    }

private fun VoucherRedemptionResponse.toVoucherRedemption(genericVoucher: String?): MarketItem.VoucherRedemption {
    return genericVoucher?.let { code ->
        Generic(code = code)
    } ?: if (vouchersExhausted) {
        Exhausted
    } else if (redeemAvailable) {
        Pending
    } else if (redeemedCode != null) {
        Redeemed(code = redeemedCode, nextRedemptionDate = redeemAvailableDate)
    } else {
        throw IllegalStateException()
    }
}
