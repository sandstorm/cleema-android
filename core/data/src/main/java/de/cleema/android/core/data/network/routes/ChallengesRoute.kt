package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.requests.AnswerChallengeRequest
import de.cleema.android.core.data.network.requests.ApiRequest
import de.cleema.android.core.data.network.requests.CreateChallengeRequest
import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.ChallengeResponse
import de.cleema.android.core.data.network.responses.ChallengeTemplateResponse
import retrofit2.http.*
import java.util.*

internal interface ChallengesRoute {
    @GET("/api/challenges?populate=*&filters[kind][\$eq]=partner,collective")
    suspend fun getChallenges(@Query("filters[region][uuid][\$eq]") id: UUID? = null): Result<ApiResponse<List<ChallengeResponse>>>

    @GET("api/challenges/{id}")
    suspend fun getChallenge(@Path("id") id: UUID): Result<ApiResponse<ChallengeResponse>>

    @PATCH("api/challenges/{id}/join")
    suspend fun joinChallenge(@Path("id") id: UUID): Result<ApiResponse<ChallengeResponse>>

    @PATCH("api/challenges/{id}/leave")
    suspend fun leaveChallenge(@Path("id") id: UUID): Result<ApiResponse<ChallengeResponse>>

    @GET("/api/challenges?populate=answers&joined=true")
    suspend fun getJoinedChallenges(): Result<ApiResponse<List<ChallengeResponse>>>

    @PATCH("api/challenges/{id}/answer")
    suspend fun answerChallenge(
        @Path("id") id: UUID,
        @Body request: AnswerChallengeRequest
    ): Result<ApiResponse<ChallengeResponse>>

    @GET("api/challenge-templates?populate=deep")
    suspend fun challengeTemplates(): Result<ApiResponse<List<ChallengeTemplateResponse>>>

    @POST("/api/challenges")
    suspend fun createChallenge(@Body request: ApiRequest<CreateChallengeRequest>): Result<ApiResponse<ChallengeResponse>>
}
