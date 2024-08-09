package de.cleema.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UUIDGenerator

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InstantGenerator

@Module
@InstallIn(SingletonComponent::class)
object GeneratorsModule {
    @Provides
    @UUIDGenerator
    fun providesUUIDGenerator(): () -> UUID = { UUID.randomUUID() }

    @Provides
    @InstantGenerator
    fun providesInstantGenerator(): () -> Instant = { Clock.System.now() }
}
