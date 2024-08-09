/*
 * Created by Kumpels and Friends on 2023-01-04
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val username: String?,
    val password: String?,
    val passwordRepeat: String?,
    val email: String?,
    val acceptsSurveys: Boolean?,
    val region: IdRequest?,
    val avatar: IdRequest?
)
