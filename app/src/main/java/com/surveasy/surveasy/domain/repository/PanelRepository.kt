package com.surveasy.surveasy.domain.repository

import com.surveasy.surveasy.domain.base.BaseState
import com.surveasy.surveasy.domain.model.AuthProvider
import com.surveasy.surveasy.domain.model.KakaoInfo
import com.surveasy.surveasy.domain.model.PanelDetailInfo
import com.surveasy.surveasy.domain.model.PanelInfo
import com.surveasy.surveasy.domain.model.PanelWatermark
import com.surveasy.surveasy.domain.model.Register
import com.surveasy.surveasy.domain.model.Token
import kotlinx.coroutines.flow.Flow

interface PanelRepository {

    fun kakaoSignup(
        name: String,
        email: String,
        phoneNumber: String,
        gender: String,
        birth: String,
        authProvider: String
    ): Flow<BaseState<KakaoInfo>>

    fun createExistPanel(
        uid: String,
        email: String,
        pw: String,
        platform: String = "ANDROID"
    ): Flow<BaseState<Token>>

    fun createNewPanel(
        platform: String,
        accountOwner: String,
        accountType: String,
        accountNumber: String,
        inflowPath: String,
        inflowPathEtc: String?,
        pushOn: Boolean,
        marketingAgree: Boolean,
    ): Flow<BaseState<Register>>

    fun queryPanelInfo(): Flow<BaseState<PanelInfo>>

    fun queryPanelDetailInfo(): Flow<BaseState<PanelDetailInfo>>

    fun queryPanelWatermarkInfo(): Flow<BaseState<PanelWatermark>>

    fun editPanelInfo(
        phoneNumber: String,
        accountOwner: String,
        accountType: String,
        accountNumber: String,
        english: Boolean,
    ): Flow<BaseState<Unit>>

    fun signout(): Flow<BaseState<Unit>>

    fun withdraw(): Flow<BaseState<AuthProvider>>
}
