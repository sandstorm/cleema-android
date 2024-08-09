/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.drawer

import de.cleema.android.R
import de.cleema.android.core.models.DrawerRoute

val DrawerRoute.titleRes: Int
    get() = when (this) {
        DrawerRoute.ABOUT -> R.string.drawer_content_about
        DrawerRoute.PRIVACYPOLICY -> R.string.drawer_content_privacy_policy
        DrawerRoute.IMPRINT -> R.string.drawer_content_imprint
        DrawerRoute.PARTNERSHIP -> R.string.drawer_content_partners
        //DrawerRoute.SPONSORSHIP -> R.string.drawer_content_sponsorship
    }


