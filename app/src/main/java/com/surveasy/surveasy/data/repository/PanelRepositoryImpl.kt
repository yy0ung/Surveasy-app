package com.surveasy.surveasy.data.repository

import com.surveasy.surveasy.data.model.request.EditInfoRequest
import com.surveasy.surveasy.data.model.request.ExistRegisterRequest
import com.surveasy.surveasy.data.model.request.KakaoInfoRequest
import com.surveasy.surveasy.data.model.request.NewRegisterRequest
import com.surveasy.surveasy.data.model.response.AuthProviderResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.model.response.KakaoInfoResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.model.response.PanelDetailInfoResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.model.response.PanelInfoResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.model.response.PanelWatermarkResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.model.response.RegisterResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.model.response.TokenResponse.Companion.toDomainModel
import com.surveasy.surveasy.data.remote.SurveasyApi
import com.surveasy.surveasy.data.remote.handleResponse
import com.surveasy.surveasy.domain.base.BaseState
import com.surveasy.surveasy.domain.model.AuthProvider
import com.surveasy.surveasy.domain.model.KakaoInfo
import com.surveasy.surveasy.domain.model.PanelDetailInfo
import com.surveasy.surveasy.domain.model.PanelInfo
import com.surveasy.surveasy.domain.model.PanelWatermark
import com.surveasy.surveasy.domain.model.Register
import com.surveasy.surveasy.domain.model.Token
import com.surveasy.surveasy.domain.repository.PanelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PanelRepositoryImpl @Inject constructor(private val api: SurveasyApi) : PanelRepository {
    override fun kakaoSignup(
        name: String,
        email: String,
        phoneNumber: String,
        gender: String,
        birth: String,
        authProvider: String
    ): Flow<BaseState<KakaoInfo>> = flow {
        when (val result =
            handleResponse {
                api.kakaoSignup(
                    KakaoInfoRequest(name, email, phoneNumber, gender, birth, authProvider)
                )
            }) {
            is BaseState.Success -> emit(BaseState.Success(result.data.toDomainModel()))
            is BaseState.Error -> emit(result)
        }
    }

    override fun createExistPanel(
        uid: String,
        email: String,
        pw: String,
        platform: String
    ): Flow<BaseState<Token>> = flow {
        when (val result =
            handleResponse {
                api.createExistPanel(
                    ExistRegisterRequest(
                        uid,
                        email,
                        pw,
                        platform
                    )
                )
            }) {
            is BaseState.Success -> emit(BaseState.Success(result.data.toDomainModel()))
            is BaseState.Error -> emit(result)
        }
    }

    override fun createNewPanel(
        platform: String,
        accountOwner: String,
        accountType: String,
        accountNumber: String,
        inflowPath: String,
        inflowPathEtc: String?,
        pushOn: Boolean,
        marketingAgree: Boolean,
    ): Flow<BaseState<Register>> = flow {
        when (val result =
            handleResponse {
                api.createNewPanel(
                    NewRegisterRequest(
                        platform,
                        accountOwner,
                        accountType,
                        accountNumber,
                        inflowPath,
                        inflowPathEtc,
                        pushOn,
                        marketingAgree
                    )
                )
            }) {
            is BaseState.Success -> emit(BaseState.Success(result.data.toDomainModel()))
            is BaseState.Error -> emit(result)
        }
    }

    override fun queryPanelInfo(): Flow<BaseState<PanelInfo>> = flow {

        when (val result = handleResponse { api.queryPanelInfo() }) {
            is BaseState.Success -> {
                emit(BaseState.Success(result.data.toDomainModel()))
            }

            is BaseState.Error -> emit(result)
        }
    }

    override fun queryPanelDetailInfo(): Flow<BaseState<PanelDetailInfo>> = flow {
        when (val result = handleResponse { api.queryPanelDetailInfo() }) {
            is BaseState.Success -> {
                emit(BaseState.Success(result.data.toDomainModel()))
            }

            is BaseState.Error -> emit(result)
        }
    }

    override fun queryPanelWatermarkInfo(): Flow<BaseState<PanelWatermark>> = flow {
        when (val result = handleResponse { api.queryPanelWatermarkInfo() }) {
            is BaseState.Success -> {
                emit(BaseState.Success(result.data.toDomainModel()))
            }

            is BaseState.Error -> emit(result)
        }
    }

    override fun editPanelInfo(
        phoneNumber: String,
        accountOwner: String,
        accountType: String,
        accountNumber: String,
        english: Boolean
    ): Flow<BaseState<Unit>> = flow {
        when (val result = handleResponse {
            api.editPanelInfo(
                EditInfoRequest(
                    phoneNumber, accountOwner, accountType, accountNumber, english
                )
            )
        }) {
            is BaseState.Success -> emit(BaseState.Success(Unit))
            is BaseState.Error -> emit(result)
        }

    }

    override fun signout(): Flow<BaseState<Unit>> = flow {
        when (val result = handleResponse { api.signout() }) {
            is BaseState.Success -> emit(BaseState.Success(Unit))
            is BaseState.Error -> emit(result)
        }
    }

    override fun withdraw(): Flow<BaseState<AuthProvider>> = flow {
        when (val result = handleResponse { api.withdraw() }) {
            is BaseState.Success -> emit(BaseState.Success(result.data.toDomainModel()))
            is BaseState.Error -> emit(result)
        }
    }

}