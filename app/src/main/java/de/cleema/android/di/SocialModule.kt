package de.cleema.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.cleema.android.social.DefaultSocialClient
import de.cleema.android.social.SocialClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocialModule {
    @Singleton
    @Provides
    fun providesSocialClient(
        @ApplicationContext context: Context,
        @BaseURI baseUri: String
    ): SocialClient {
        return DefaultSocialClient(
            context,
            baseUri = baseUri
        )
    }
}

