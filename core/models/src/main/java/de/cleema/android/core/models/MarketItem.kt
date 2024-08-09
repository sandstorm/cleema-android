/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import kotlinx.datetime.Instant
import java.util.*
import kotlin.random.Random

data class MarketItem(
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val subTitle: String = "",
    val description: String = "",
    val discount: Int,
    val image: RemoteImage? = null,
    val region: Region? = null,
    val location: Location? = null,
    val storeType: StoreType = StoreType.Online,
    val voucherRedemption: VoucherRedemption = VoucherRedemption.Pending,
    val address: Address? = null,
    val websiteUrl: String? = null
) {
    enum class StoreType {
        Online,
        Shop
    }

    sealed interface VoucherRedemption {
        object Pending : VoucherRedemption
        data class Redeemed(val code: String, val nextRedemptionDate: Instant? = null) : VoucherRedemption
        object Exhausted : VoucherRedemption
        data class Generic(val code: String) : VoucherRedemption
    }

    companion object {
        val demo = arrayListOf(
            MarketItem(
                id = UUID.randomUUID(),
                title = "Lose",
                subTitle = "Unverpackt Laden",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=1", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Unipolar lorem ipsum bla",
                subTitle = "Faire Bio-Kleidung",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(0, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=2", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Juliette Beke",
                subTitle = "Friseursalon",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(0, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=3", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "pandoo",
                subTitle = "Bambus-Produkte",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = 0,
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=4", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Lose",
                subTitle = "Unverpackt Laden",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=5", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Unipolar",
                subTitle = "Faire Bio-Kleidung",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=6", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Juliette Beke",
                subTitle = "Friseursalon",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=7", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "pandoo",
                subTitle = "Bambus-Produkte",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=8", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Lose",
                subTitle = "Unverpackt Laden",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=9", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Unipolar",
                subTitle = "Faire Bio-Kleidung",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=10", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Juliette Beke",
                subTitle = "Friseursalon",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=11", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "pandoo",
                subTitle = "Bambus-Produkte",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=12", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Lose",
                subTitle = "Unverpackt Laden",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=13", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Unipolar",
                subTitle = "Faire Bio-Kleidung",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=14", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "Juliette Beke",
                subTitle = "Friseursalon",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=15", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
            MarketItem(
                id = UUID.randomUUID(),
                title = "pandoo",
                subTitle = "Bambus-Produkte",
                description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes,",
                discount = Random.nextInt(1, 50),
                image = RemoteImage.of(url = "https://loremflickr.com/1035/624?random=16", size = Size(345f, 208f)),
                region = Region(UUID.randomUUID(), name = "leipzig"),
                location = Location.LEIPZIG,
            ),
        )
    }
}

