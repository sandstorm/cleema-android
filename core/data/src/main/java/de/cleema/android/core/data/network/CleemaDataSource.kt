/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import de.cleema.android.core.data.network.requests.*
import de.cleema.android.core.data.network.responses.*
import de.cleema.android.core.data.network.routes.*
import de.cleema.android.core.models.Challenge.Interval
import de.cleema.android.core.models.Challenge.Interval.*
import de.cleema.android.core.models.DrawerRoute
import de.cleema.android.core.models.EditChallenge
import de.cleema.android.data.network.ResultCallAdapterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.http.*
import java.net.URL
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class CleemaException(override val message: String?) :
    Throwable(message = "Could not authenticate user")

private interface CleemaAPI : MagazineRoute, ProjectsRoute, RegionsRoute, InfoRoute, QuizRoute,
    AuthRoute, UsersRoute, ChallengesRoute, MarketplaceRoute, TrophiesRoute, SurveysRoute, AvatarRoute, SponsorRoute

@Singleton
class CleemaDataSource @Inject constructor(
    private val json: Json,
    val baseURL: URL,
    val baseToken: String
) :
    RegionDataSource,
    AuthDataSource,
    InfoDataSource,
    QuizDataSource,
    ChallengesDataSource,
    MarketplaceDataSource,
    ProjectsDataSource,
    MagazineDataSource,
    TrophiesDataSource,
    UsersDataSource,
    SurveysDataSource,
    AvatarDataSource,
    SponsorDataSource {
    private var authResponse: AuthResponse? = null
    override var clientId: UUID? = null

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.addHeader(
                    "Authorization",
                    "bearer ${this.authResponse?.jwt ?: baseToken}"
                )
                clientId?.let {
                    requestBuilder.addHeader(
                        name = "cleema-install-id",
                        value = it.toString()
                    )
                }
                requestBuilder.header("Content-Type", "application/json")
                chain.proceed(requestBuilder.build())
            }
//            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .addCallAdapterFactory(ResultCallAdapterFactory(json))
            .baseUrl(baseURL)
            .client(client)
            .build()
    }
    private val api: CleemaAPI by lazy {
        retrofit.create(CleemaAPI::class.java)
    }

    override suspend fun delete(id: UUID): Result<UpdateUserResponse> = api.delete(id)

    override suspend fun getProjects(regionId: UUID?, joined: Boolean?): Result<List<ProjectResponse>> =
        api.getProjects(regionId, joined).mapCatching(ApiResponse<List<ProjectResponse>>::valueOrError)

    override suspend fun getProject(projectId: UUID): Result<ProjectResponse> =
        api.getProject(projectId).mapCatching(ApiResponse<ProjectResponse>::valueOrError)

    override suspend fun getFavedProjects(): Result<List<ProjectResponse>> =
        api.getFavedProjects().mapCatching(ApiResponse<List<ProjectResponse>>::valueOrError)

    override suspend fun getMagazineItems(regionId: UUID?): Result<List<MagazineItemResponse>> =
        api.getMagazineItems(regionId).mapCatching(ApiResponse<List<MagazineItemResponse>>::valueOrError)

    override suspend fun getFavedMagazineItems(): Result<List<MagazineItemResponse>> =
        api.getFavedMagazineItems().mapCatching(ApiResponse<List<MagazineItemResponse>>::valueOrError)

    override suspend fun getMagazineItem(itemId: UUID): Result<MagazineItemResponse> =
        api.getMagazineItem(itemId).mapCatching(ApiResponse<MagazineItemResponse>::valueOrError)

    override suspend fun favMagazineItem(itemId: UUID, faved: Boolean): Result<MagazineItemResponse> = if (faved) {
        api.favMagazineItem(itemId)
    } else {
        api.unfavMagazineItem(itemId)
    }.mapCatching(ApiResponse<MagazineItemResponse>::valueOrError)

    override suspend fun markMagazineItemAsRead(itemId: UUID): Result<MagazineItemResponse> =
        api.markMagazineItemAsRead(itemId).mapCatching(ApiResponse<MagazineItemResponse>::valueOrError)

    override suspend fun favProject(projectId: UUID, faved: Boolean): Result<ProjectResponse> =
        if (faved) {
            api.favProject(projectId)
        } else {
            api.unfavProject(projectId)
        }.mapCatching(ApiResponse<ProjectResponse>::valueOrError)

    override suspend fun joinProject(projectId: UUID): Result<ProjectResponse> =
        api.joinProject(projectId).mapCatching(ApiResponse<ProjectResponse>::valueOrError)

    override suspend fun leaveProject(projectId: UUID): Result<ProjectResponse> {
        return api.leaveProject(projectId).mapCatching(ApiResponse<ProjectResponse>::valueOrError)
    }

    override suspend fun getRegions(regionId: UUID?): Result<List<RegionResponse>> =
        api.getRegions(regionId).mapCatching(ApiResponse<List<RegionResponse>>::valueOrError)

    override suspend fun login(userName: String, password: String): Result<AuthResponse> {
        authResponse = null
        return api.login(userName, password).onSuccess {
            this.authResponse = it
        }
    }

    override suspend fun register(userRequest: CreateUserRequest): Result<AuthResponse> {
        authResponse = null
        return api.registerUser(userRequest).onSuccess {
            this.authResponse = it
        }
    }

    override suspend fun logout() {
        authResponse = null
    }

    override suspend fun me(): Result<UserResponse> = api.getMe().mapCatching { it.valueOrError().user }
    override suspend fun getContent(route: DrawerRoute): Result<String> =
        api.getContent(route.route).mapCatching { it.valueOrError().content }

    override suspend fun getQuizState(regionId: UUID?): Result<QuizResponse> =
        api.getQuiz(regionId).mapCatching(ApiResponse<QuizResponse>::valueOrError)

    override suspend fun answerQuiz(
        id: UUID,
        answer: de.cleema.android.core.models.Quiz.Choice
    ): Result<AnswerQuizResponse> =
        api.answerQuiz(ApiRequest(AnswerQuizRequest(id, answer.answer)))
            .mapCatching(ApiResponse<AnswerQuizResponse>::valueOrError)

    override suspend fun getChallenges(regionId: UUID?): Result<List<ChallengeResponse>> =
        api.getChallenges(regionId).mapCatching(ApiResponse<List<ChallengeResponse>>::valueOrError)

    override suspend fun getChallenge(challengeId: UUID): Result<ChallengeResponse> =
        api.getChallenge(challengeId).mapCatching(ApiResponse<ChallengeResponse>::valueOrError)

    override suspend fun joinChallenge(challengeId: UUID): Result<ChallengeResponse> =
        api.joinChallenge(challengeId).mapCatching(ApiResponse<ChallengeResponse>::valueOrError)

    override suspend fun leaveChallenge(challengeId: UUID): Result<ChallengeResponse> =
        api.leaveChallenge(challengeId).mapCatching(ApiResponse<ChallengeResponse>::valueOrError)

    override suspend fun getJoinedChallenges(): Result<List<ChallengeResponse>> =
        api.getJoinedChallenges().mapCatching(ApiResponse<List<ChallengeResponse>>::valueOrError)

    override suspend fun answerChallenge(
        challengeId: UUID,
        answers: Map<Int, de.cleema.android.core.models.JoinedChallenge.Answer>
    ): Result<ChallengeResponse> = api.answerChallenge(
        challengeId,
        AnswerChallengeRequest(answers.map {
            AnswerChallengeRequest.AnswerRequest(
                it.key,
                it.value.answer
            )
        })
    ).mapCatching(ApiResponse<ChallengeResponse>::valueOrError)

    override suspend fun getChallengeTemplates(): Result<List<ChallengeTemplateResponse>> =
        api.challengeTemplates().mapCatching(ApiResponse<List<ChallengeTemplateResponse>>::valueOrError)

    override suspend fun createChallenge(template: EditChallenge, participants: Set<UUID>): Result<ChallengeResponse> =
        template.toRequest(participants)?.let { request ->
            api.createChallenge(ApiRequest(request)).mapCatching { it.valueOrError() }
        } ?: Result.failure(java.lang.IllegalArgumentException("No region specified"))

    override suspend fun getOffers(regionId: UUID?): Result<List<OfferResponse>> =
        api.getOffers(regionId).mapCatching(ApiResponse<List<OfferResponse>>::valueOrError)

    override suspend fun getOffer(offerId: UUID) =
        api.getOffer(offerId).mapCatching(ApiResponse<OfferResponse>::valueOrError)

    override suspend fun redeem(offerId: UUID): Result<OfferResponse> =
        api.redeemOffer(offerId).mapCatching(ApiResponse<OfferResponse>::valueOrError)

    override suspend fun getTrophies(): Result<List<TrophyItemResponse>> =
        api.getTrophies().mapCatching(ApiResponse<List<TrophyItemResponse>>::valueOrError)

    override suspend fun getTrophy(trophyId: UUID): Result<TrophyItemResponse> =
        api.getTrophy(trophyId).mapCatching(ApiResponse<TrophyItemResponse>::valueOrError)

    override suspend fun followInvitation(referralCode: String): Result<SocialGraphItemResponse> =
        api.followInvitation(referralCode).mapCatching(ApiResponse<SocialGraphItemResponse>::valueOrError)

    override suspend fun follows(): Result<SocialGraphResponse> =
        api.follows().mapCatching(ApiResponse<SocialGraphResponse>::valueOrError)

    override suspend fun unfollow(socialGraphItemId: UUID): Result<SocialGraphResponse> =
        api.unfollow(socialGraphItemId).mapCatching(ApiResponse<SocialGraphResponse>::valueOrError)

    override suspend fun updateUser(id: UUID, request: UpdateUserRequest): Result<UpdateUserResponse> =
        api.updateUser(id, request)

    override suspend fun getSurveys() =
        api.getSurveys().mapCatching(ApiResponse<List<SurveyResponse>>::valueOrError)

    override suspend fun evaluate(id: UUID): Result<SurveyResponse> =
        api.evaluateSurvey(id).mapCatching(ApiResponse<SurveyResponse>::valueOrError)

    override suspend fun participate(id: UUID): Result<SurveyResponse> =
        api.participateSurvey(id).mapCatching(ApiResponse<SurveyResponse>::valueOrError)

    override suspend fun getAvatarList(): Result<List<IdentifiedImageResponse>> =
        api.getAvatarList().mapCatching(ApiResponse<List<IdentifiedImageResponse>>::valueOrError)

    override suspend fun becomeSponsor(request: BecomeSponsorRequest): Result<kotlin.Unit> = api.becomeSponsor(
        ApiRequest(request)
    )
}

private fun EditChallenge.toRequest(participants: Set<UUID>): CreateChallengeRequest? = regionId?.let { edit ->
    CreateChallengeRequest(
        title = title,
        teaserText = teaserText,
        description = description,
        startDate = start,
        endDate = end,
        kind = if (participants.isNotEmpty()) ApiChallengeKind.group else kind.toApi(),
        isPublic = isPublic,
        interval = interval.toApi(),
        goalType = goalType.toApi(),
        goalSteps = toApiSteps(),
        goalMeasurement = toApiMeasurement(),
        participants = participants.map { it.toString() },
        region = IdRequest(edit),
        image = image?.let { IdRequest(it.id) }
    )
}

private fun de.cleema.android.core.models.Challenge.Kind.toApi(): ApiChallengeKind = when (this) {
    is de.cleema.android.core.models.Challenge.Kind.Group -> ApiChallengeKind.group
    is de.cleema.android.core.models.Challenge.Kind.Partner -> ApiChallengeKind.partner
    is de.cleema.android.core.models.Challenge.Kind.Collective -> ApiChallengeKind.collective
    de.cleema.android.core.models.Challenge.Kind.User -> ApiChallengeKind.user
}

private fun de.cleema.android.core.models.Challenge.GoalType.toApi(): ApiGoalType = when (this) {
    is de.cleema.android.core.models.Challenge.GoalType.Measurement -> ApiGoalType.measurement
    is de.cleema.android.core.models.Challenge.GoalType.Steps -> ApiGoalType.steps
}

private fun EditChallenge.toApiMeasurement(): ApiGoalMeasurement? = when (val goalType = goalType) {
    is de.cleema.android.core.models.Challenge.GoalType.Measurement -> ApiGoalMeasurement(
        value = goalType.count.toUInt(),
        unit = goalType.unit.toApi()
    )
    is de.cleema.android.core.models.Challenge.GoalType.Steps -> null
}

private fun de.cleema.android.core.models.Challenge.Unit.toApi() = when (this) {
    de.cleema.android.core.models.Challenge.Unit.KILOMETERS -> ApiGoalMeasurement.Unit.km
    de.cleema.android.core.models.Challenge.Unit.KILOGRAMS -> ApiGoalMeasurement.Unit.kg
}

private fun EditChallenge.toApiSteps(): ApiSteps? = when (val goalType = goalType) {
    is de.cleema.android.core.models.Challenge.GoalType.Measurement -> null
    is de.cleema.android.core.models.Challenge.GoalType.Steps -> ApiSteps(goalType.count.toUInt())
}

private fun Interval.toApi(): ApiChallengeInterval = when (this) {
    DAILY -> ApiChallengeInterval.daily
    WEEKLY -> ApiChallengeInterval.weekly
}

fun <T> ApiResponse<T>.valueOrError(): T {
    return data ?: error?.let { throw CleemaException(it.message) }
    ?: throw CleemaException("Unknown error")
}

private val de.cleema.android.core.models.Quiz.Choice.answer: QuizAnswer
    get() = when (this) {
        de.cleema.android.core.models.Quiz.Choice.A -> QuizAnswer.A
        de.cleema.android.core.models.Quiz.Choice.B -> QuizAnswer.B
        de.cleema.android.core.models.Quiz.Choice.C -> QuizAnswer.C
        de.cleema.android.core.models.Quiz.Choice.D -> QuizAnswer.D
    }
private val de.cleema.android.core.models.JoinedChallenge.Answer.answer: AnswerChallengeRequest.Value
    get() = when (this) {
        de.cleema.android.core.models.JoinedChallenge.Answer.FAILED -> AnswerChallengeRequest.Value.failed
        de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED -> AnswerChallengeRequest.Value.succeeded
    }
private val DrawerRoute.route: String
    get() = when (this) {
        DrawerRoute.ABOUT -> "about"
        DrawerRoute.PRIVACYPOLICY -> "privacy-policy"
        DrawerRoute.IMPRINT -> "legal-notice"
        DrawerRoute.PARTNERSHIP -> "partnership"
        // DrawerRoute.SPONSORSHIP -> "sponsor-membership"
    }
