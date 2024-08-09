/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import java.util.*

data class UserDetails(
    val name: String = "",
    val email: String = "",
    val region: Region? = null,
    val avatar: IdentifiedImage? = null,
    val password: String = "",
    val passwordConfirmation: String = "",
    val previousPassword: String = "",
    val acceptsSurveys: Boolean = false,
    val localUserId: UUID? = null
)
