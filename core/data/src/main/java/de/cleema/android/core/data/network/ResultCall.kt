/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.data.network

import de.cleema.android.core.data.network.responses.ApiResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ResultCall<T>(private val delegate: Call<T>, private val json: Json) :
    Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                response.code(),
                                Result.success(response.body()!!)
                            )
                        )
                    } else {
                        response.errorBody()?.let { responseBody ->
                            val value = json.decodeFromString(responseBody.string()) as? ApiResponse<String>
                            value?.error?.let {
                                callback.onResponse(
                                    this@ResultCall,
                                    Response.success(
                                        Result.failure(
                                            RuntimeException(it.message)
                                        )
                                    )
                                )
                            }
                        } ?: callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                Result.failure(
                                    HttpException(response)
                                )
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    val errorMessage = when (t) {
                        is IOException -> "No internet connection"
                        is HttpException -> "Something went wrong!"
                        else -> t.localizedMessage
                    }
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(RuntimeException(errorMessage, t)))
                    )
                }
            }
        )
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun execute(): Response<Result<T>> {
        return Response.success(Result.success(delegate.execute().body()!!))
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun clone(): Call<Result<T>> {
        return ResultCall(delegate.clone(), json)
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}
