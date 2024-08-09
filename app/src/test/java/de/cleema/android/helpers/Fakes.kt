/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.models.IdentifiedImage
import de.cleema.android.core.models.RemoteImage

val LEIPZIG = de.cleema.android.core.models.Region(name = "Leipzig")
val DRESDEN = de.cleema.android.core.models.Region(name = "Dresden")
val PIRNA = de.cleema.android.core.models.Region(name = "Pirna")
val FAKE_LOCAL = de.cleema.android.core.models.User(
    name = "Fake local user",
    region = LEIPZIG,
    kind = de.cleema.android.core.models.User.Kind.Local,
    isSupporter = false,
    avatar = IdentifiedImage(
        image = RemoteImage(
            "http://localhost/localimage"
        )
    )
)
val FAKE_REMOTE = de.cleema.android.core.models.User(
    name = "Fake remote user", region = DRESDEN, kind = de.cleema.android.core.models.User.Kind.Remote(
        "pw", "mail@mail.com", token = "token"
    ), isSupporter = false,
    avatar = IdentifiedImage(
        image = RemoteImage(
            "http://localhost/remoteimage"
        )
    )
)

val FAKE_PARTNER =
    de.cleema.android.core.models.Partner(
        name = "Fake partner",
        url = "http://loremipsum.org",
        description = "Fake partner description"
    )
