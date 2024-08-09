/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.di

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.cleema.android.core.models.Location
import javax.inject.Singleton

interface UrlOpener {
    suspend fun openUrl(uriString: String)
}

interface LocationOpener {
    suspend fun openLocation(location: de.cleema.android.core.models.Location)
}

interface MailOpener {
    suspend fun openMail(recipient: String, subject: String, body: String)
}

@Module
@InstallIn(SingletonComponent::class)
object ExternalContentModule {
    @Singleton
    @Provides
    fun providesUrlOpener(
        @ApplicationContext context: Context
    ): UrlOpener = object : UrlOpener {
        override suspend fun openUrl(uriString: String) {
            try {
                val intent = Intent(ACTION_VIEW, Uri.parse(uriString)).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Throwable) {
                println(e.localizedMessage)
            }
        }
    }

    @Singleton
    @Provides
    fun providesLocationOpener(
        @ApplicationContext context: Context
    ): LocationOpener = object : LocationOpener {
        override suspend fun openLocation(location: de.cleema.android.core.models.Location) {
            try {
                val gmmIntentUri =
                    Uri.parse("geo:0,0?q=${location.coordinates.latitude},${location.coordinates.longitude}(${location.title})")
                val mapIntent = Intent(ACTION_VIEW, gmmIntentUri).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            } catch (e: Throwable) {
                println(e.localizedMessage)
            }
        }
    }

    @Singleton
    @Provides
    fun providesMailOpener(
        @ApplicationContext context: Context
    ): MailOpener = object : MailOpener {
        override suspend fun openMail(recipient: String, subject: String, body: String) {
            try {
                val intent = Intent(
                    ACTION_VIEW,
                    Uri.parse("mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
                ).apply {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Throwable) {
                println(e.localizedMessage)
            }
        }
    }
}
