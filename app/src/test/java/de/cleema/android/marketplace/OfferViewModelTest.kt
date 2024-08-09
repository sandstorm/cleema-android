/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.marketplace

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.models.Location
import de.cleema.android.core.models.MarketItem
import de.cleema.android.core.models.MarketItem.VoucherRedemption.Redeemed
import de.cleema.android.helpers.FakeLocationOpener
import de.cleema.android.helpers.FakeMarketplaceRepository
import de.cleema.android.helpers.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfferViewModelTest {
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var mapOpener: FakeLocationOpener
    private lateinit var repository: FakeMarketplaceRepository
    private lateinit var sut: OfferViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeMarketplaceRepository()
        mapOpener = FakeLocationOpener()
        savedStateHandle = SavedStateHandle()
        sut = OfferViewModel(repository, mapOpener, savedStateHandle)
    }

    private fun given(item: MarketItem, code: String? = null) {
        savedStateHandle["marketplaceId"] = item.id.toString()
        repository.stubbedOffers.trySend(Result.success(item))
        repository.stubbedVouchers.trySend(code)
    }

    @Test
    fun `Click on coordinate will invoke the opener`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(OfferUiState.Loading, sut.uiState.value)
        val item = MarketItem(
            title = "Offer",
            description = "Description",
            discount = 42,
            location = listOf(Location.LEIPZIG, Location.PIRNA, Location.DRESDEN).random(),
        )
        given(item)
        assertEquals(OfferUiState.Success(item), sut.uiState.value)

        sut.onMapClicked()

        assertEquals(item.location!!, mapOpener.openedLocation)

        collectJob.cancel()
    }

    @Test
    fun `It saved on redemption`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val item = MarketItem(
            title = "Offer",
            description = "Description",
            discount = 42,
            location = Location.LEIPZIG,
            voucherRedemption = Redeemed("1234", Instant.DISTANT_FUTURE)
        )
        given(item)
        assertEquals(OfferUiState.Success(item), sut.uiState.value)

        sut.onRedeemClick()

        repository.stubbedVouchers.trySend(null)
        assertEquals(item.id to "1234", repository.redeemed)

        collectJob.cancel()
    }

    @Test
    fun `It updates on according to the code flow in the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val item = MarketItem(
            title = "Offer",
            description = "Description",
            discount = 42,
            location = Location.LEIPZIG,
            voucherRedemption = Redeemed("1234", Instant.DISTANT_FUTURE)
        )
        given(item)
        assertEquals(OfferUiState.Success(item), sut.uiState.value)

        repository.stubbedVouchers.trySend("deadbeef")

        assertEquals(
            OfferUiState.Success(item.copy(voucherRedemption = MarketItem.VoucherRedemption.Exhausted)),
            sut.uiState.value
        )

        collectJob.cancel()
    }
}
