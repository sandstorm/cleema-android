/*
 * Created by Kumpels and Friends on 2023-01-30
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.di

import android.content.Context
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.cleema.android.BuildConfig
import de.cleema.android.R
import de.cleema.android.becomesponsor.DefaultIBANValidator
import de.cleema.android.becomesponsor.IBANValidator
import de.cleema.android.core.data.*
import de.cleema.android.core.data.network.*
import de.cleema.android.data.network.CleemaConfirmationDataSource
import de.cleema.android.registeruser.RegisterUserUiState.Edit.Validation
import de.cleema.android.registeruser.RegisterUserUiState.Edit.Validation.*
import de.cleema.android.registeruser.ValidationFormatter
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope.*
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiBaseURL

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseURI

@Module
@InstallIn(SingletonComponent::class)
object UrlModule {
    @Provides
    @ApiBaseURL
    fun providesAPIBaseUrl() = URL(BuildConfig.apiUrl)

    @Provides
    @BaseURI
    fun providesBaseUri() = BuildConfig.baseUrl
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideProjectsRepository(
        networkDataSource: ProjectsDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApiBaseURL baseURL: URL
    ): ProjectsRepository {
        return DefaultProjectsRepository(
            networkDataSource,
            baseURL = baseURL,
            ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideMagazineRepository(
        networkDataSource: MagazineDataSource,
        @ApiBaseURL baseURL: URL
//        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): MagazineRepository {
        return DefaultMagazineRepository(networkDataSource, baseURL = baseURL)
    }

    @Singleton
    @Provides
    fun provideMarketplaceRepository(
        marketplaceDataSource: MarketplaceDataSource,
        @ApiBaseURL baseURL: URL,
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): MarketplaceRepository {
        return DefaultMarketplaceRepository(
            marketplaceDataSource,
            baseURL = baseURL,
            ioDispatcher = ioDispatcher,
            context = context
        )
    }

    @Singleton
    @Provides
    fun provideRegionsRepository(
        networkDataSource: RegionDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): RegionsRepository {
        return DefaultRegionsRepository(
            networkDataSource,
            ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        @ApplicationContext context: Context,
        usersDataSource: UsersDataSource,
        @ApiBaseURL baseURL: URL
    ): UserRepository {
        return DefaultUserRepository(context, usersDataSource, baseURL = baseURL)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        usersDataSource: UsersDataSource,
        confirmDataSource: ConfirmDataSource,
        @ApiBaseURL baseURL: URL
    ): AuthRepository = NetworkAuthRepository(authDataSource, usersDataSource, confirmDataSource, baseURL)

    @Singleton
    @Provides
    fun provideDrawerContentRepository(
        infoDatasource: InfoDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): InfoRepository {
        return DefaultInfoRepository(infoDatasource, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideQuizRepository(
        quizDataSource: QuizDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): QuizRepository {
        return DefaultQuizRepository(quizDataSource, ioDispatcher)
    }

    @Singleton
    @Provides
    fun providesChallengesRepository(
        challengesDataSource: ChallengesDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApiBaseURL baseURL: URL
    ): ChallengesRepository = DefaultChallengesRepository(challengesDataSource, ioDispatcher, baseURL)

    @Singleton
    @Provides
    fun providesTrophiesRepository(
        trophiesDataSource: TrophiesDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApiBaseURL baseURL: URL
    ): TrophiesRepository = DefaultTrophiesRepository(trophiesDataSource, ioDispatcher, baseURL)

    @Singleton
    @Provides
    fun providesSurveysRepository(
        surveysDataSource: SurveysDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): SurveysRepository = DefaultSurveysRepository(surveysDataSource, ioDispatcher)

    @Singleton
    @Provides
    fun providesAvatarRepository(
        avatarDataSource: AvatarDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApiBaseURL baseURL: URL
    ): AvatarRepository = DefaultAvatarRepository(avatarDataSource, ioDispatcher, baseURL)

    @Singleton
    @Provides
    fun providesSponsorRepository(
        dataSource: SponsorDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): SponsorRepository = DefaultSponsorRepository(dataSource, ioDispatcher)

    @Singleton
    @Provides
    fun providesIbanValidator(
    ): IBANValidator = DefaultIBANValidator()

    @Singleton
    @Provides
    fun providesCleemaDataSource(
        json: Json,
        @ApiBaseURL baseURL: URL,
    ): CleemaDataSource = CleemaDataSource(json, baseURL, BuildConfig.baseToken)

    @Singleton
    @Provides
    fun providesConfirmDataSource(
        json: Json,
        @ApiBaseURL baseURL: URL,
    ): ConfirmDataSource = CleemaConfirmationDataSource(baseURL, json)

    @Singleton
    @Provides
    fun providesPartnerRepository(
    ): PartnerRepository = DefaultPartnerRepository()

    @Singleton
    @Provides
    fun providesValidationFormatter(
        @ApplicationContext context: Context
    ): ValidationFormatter = object : ValidationFormatter {
        override fun format(validation: Validation): String {
            return when (validation) {
                NAME -> context.getString(R.string.name_validation)
                MAIL -> context.getString(R.string.mail_validation)
                PASSWORD -> context.getString(R.string.password_validation)
                PASSWORD_CONFIRMATION -> context.getString(R.string.confirmation_validation)
                REGION -> context.getString(R.string.region_validation)
            }
        }
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindRegionDataSource(
        dataSource: CleemaDataSource
    ): RegionDataSource

    @Singleton
    @Binds
    abstract fun bindAboutDataSource(
        dataSource: CleemaDataSource
    ): InfoDataSource

    @Singleton
    @Binds
    abstract fun bindQuizDataSource(
        dataSource: CleemaDataSource
    ): QuizDataSource

    @Singleton
    @Binds
    abstract fun bindAuthDataSource(
        dataSource: CleemaDataSource
    ): AuthDataSource

    @Singleton
    @Binds
    abstract fun bindAChallengesDataSource(
        dataSource: CleemaDataSource
    ): ChallengesDataSource

    @Singleton
    @Binds
    abstract fun bindMarketplaceDataSource(
        dataSource: CleemaDataSource
    ): MarketplaceDataSource

    @Singleton
    @Binds
    abstract fun bindProjectsDataSource(
        dataSource: CleemaDataSource
    ): ProjectsDataSource

    @Singleton
    @Binds
    abstract fun bindMagazineDataSource(
        dataSource: CleemaDataSource
    ): MagazineDataSource

    @Singleton
    @Binds
    abstract fun bindTrophiesDataSource(
        dataSource: CleemaDataSource
    ): TrophiesDataSource

    @Singleton
    @Binds
    abstract fun bindSurveysDataSource(
        dataSource: CleemaDataSource
    ): SurveysDataSource

    @Singleton
    @Binds
    abstract fun bindUsersDataSource(
        dataSource: CleemaDataSource
    ): UsersDataSource

    @Singleton
    @Binds
    abstract fun bindAvatarDataSource(
        dataSource: CleemaDataSource
    ): AvatarDataSource

    @Singleton
    @Binds
    abstract fun bindSponsorDataSource(
        dataSource: CleemaDataSource
    ): SponsorDataSource
}



