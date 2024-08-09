/*
 * Created by Kumpels and Friends on 2022-12-22
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class BroadCaster<Value> {
    private val reloadChannel = MutableSharedFlow<Value>()
    val events: SharedFlow<Value> = reloadChannel.asSharedFlow()

    suspend fun post(value: Value) {
        reloadChannel.emit(value)
    }
}
