package com.surveasy.surveasy.data.remote

import com.surveasy.surveasy.data.model.request.EditInfoRequest
import com.surveasy.surveasy.data.model.request.ExistRegisterRequest
import com.surveasy.surveasy.data.model.request.FsRequest
import com.surveasy.surveasy.data.model.request.KakaoInfoRequest
import com.surveasy.surveasy.data.model.request.NewRegisterRequest
import com.surveasy.surveasy.data.model.request.RefreshTokenRequest
import com.surveasy.surveasy.data.model.request.ResponseImgRequest
import com.surveasy.surveasy.data.model.response.AccountInfoResponse
import com.surveasy.surveasy.data.model.response.AuthProviderResponse
import com.surveasy.surveasy.data.model.response.CommonIdResponse
import com.surveasy.surveasy.data.model.response.HistoryResponse
import com.surveasy.surveasy.data.model.response.HomeSurveyResponse
import com.surveasy.surveasy.data.model.response.KakaoInfoResponse
import com.surveasy.surveasy.data.model.response.PanelDetailInfoResponse
import com.surveasy.surveasy.data.model.response.PanelInfoResponse
import com.surveasy.surveasy.data.model.response.PanelWatermarkResponse
import com.surveasy.surveasy.data.model.response.RegisterResponse
import com.surveasy.surveasy.data.model.response.SurveyDetailInfoResponse
import com.surveasy.surveasy.data.model.response.SurveyResponse
import com.surveasy.surveasy.data.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SurveasyApi {

    @POST("panel/reissue")
    suspend fun createNewAccessToken(
        @Body body: RefreshTokenRequest
    ): Response<TokenResponse>

    @POST("panel/signin")
    suspend fun createExistPanel(
        @Body body: ExistRegisterRequest
    ): Response<TokenResponse>

    @POST("panel/oauth2")
    suspend fun kakaoSignup(
        @Body oAuth2UserInfo: KakaoInfoRequest,
    ): Response<KakaoInfoResponse>

    @POST("panel/signup")
    suspend fun createNewPanel(
        @Body body: NewRegisterRequest
    ): Response<RegisterResponse>

    @GET("panel")
    suspend fun queryPanelDetailInfo(): Response<PanelDetailInfoResponse>

    @PATCH("panel")
    suspend fun editPanelInfo(
        @Body body: EditInfoRequest
    ): Response<Unit>

    @GET("panel/watermark")
    suspend fun queryPanelWatermarkInfo(): Response<PanelWatermarkResponse>

    @GET("panel/signout")
    suspend fun signout(): Response<Unit>

    @DELETE("panel/withdraw")
    suspend fun withdraw(): Response<AuthProviderResponse>

    //home
    @GET("panel/home")
    suspend fun queryPanelInfo(): Response<PanelInfoResponse>

    //survey
    @GET("survey/app")
    suspend fun listSurvey(
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("sort") sort: List<String>?
    ): Response<SurveyResponse>

    @GET("survey/app/home")
    suspend fun listHomeSurvey(): Response<HomeSurveyResponse>

    @GET("survey/app/{surveyId}")
    suspend fun querySurveyDetail(@Path("surveyId") sid: Int): Response<SurveyDetailInfoResponse>

    @POST("response/{surveyId}")
    suspend fun createResponse(
        @Path("surveyId") sid: Int,
        @Body body: ResponseImgRequest
    ): Response<Unit>

    @PATCH("response/{surveyId}")
    suspend fun editResponse(
        @Path("surveyId") sid: Int,
        @Body body: ResponseImgRequest
    ): Response<CommonIdResponse>

    @POST("panel/first-survey")
    suspend fun createFsResponse(
        @Body body: FsRequest
    ): Response<Unit>

    //history
    @GET("panel/response")
    suspend fun queryAccountInfo(): Response<AccountInfoResponse>

    @GET("response/{type}")
    suspend fun listHistory(
        @Path("type") type: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("sort") sort: List<String>?
    ): Response<HistoryResponse>
}