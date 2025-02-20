package com.surveasy.surveasy.presentation.main.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surveasy.surveasy.domain.base.BaseState
import com.surveasy.surveasy.domain.usecase.CreateResponseUseCase
import com.surveasy.surveasy.domain.usecase.LoadImageUseCase
import com.surveasy.surveasy.domain.usecase.QueryPanelWatermarkInfoUseCase
import com.surveasy.surveasy.domain.usecase.QuerySurveyDetailUseCase
import com.surveasy.surveasy.presentation.main.survey.mapper.toSurveyDetailData
import com.surveasy.surveasy.presentation.main.survey.mapper.toSurveyWatermarkData
import com.surveasy.surveasy.presentation.util.ErrorCode.CODE_400
import com.surveasy.surveasy.presentation.util.ErrorCode.CODE_404
import com.surveasy.surveasy.presentation.util.ErrorCode.CODE_409
import com.surveasy.surveasy.presentation.util.ErrorMsg.DATA_ERROR
import com.surveasy.surveasy.presentation.util.ErrorMsg.DID_SURVEY_ERROR
import com.surveasy.surveasy.presentation.util.ErrorMsg.INVALID_SURVEY_ERROR
import com.surveasy.surveasy.presentation.util.ErrorMsg.NOT_ALLOW_SURVEY_ERROR
import com.surveasy.surveasy.presentation.util.ErrorMsg.RETRY
import com.surveasy.surveasy.presentation.util.ErrorMsg.SURVEY_ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val querySurveyDetailUseCase: QuerySurveyDetailUseCase,
    private val loadImageUseCase: LoadImageUseCase,
    private val createResponseUseCase: CreateResponseUseCase,
    private val queryPanelWatermarkInfoUseCase: QueryPanelWatermarkInfoUseCase,
) : ViewModel() {
    private val sId = MutableStateFlow(0)
    val timer = MutableStateFlow(3)

    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SurveyEvents>(
        replay = 0,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<SurveyEvents> = _events.asSharedFlow()


    suspend fun createResponse(uri: String, name: String) {
        _events.emit(SurveyEvents.ShowLoading)
        val imgUrl = viewModelScope.async {
            loadImageUseCase(uri, sId.value, name)
        }.await()

        if (imgUrl.isEmpty()) {
            _events.emit(SurveyEvents.DismissLoading)
            _events.emit(SurveyEvents.ShowSnackBar(SURVEY_ERROR))
        } else {
            createResponseUseCase(sId.value, imgUrl).onEach { state ->
                when (state) {
                    is BaseState.Success -> _events.emit(SurveyEvents.NavigateToDone)
                    is BaseState.Error -> {
                        _events.emit(
                            when (state.code) {
                                CODE_400 -> SurveyEvents.ShowSnackBar(NOT_ALLOW_SURVEY_ERROR)
                                CODE_404 -> SurveyEvents.ShowSnackBar(INVALID_SURVEY_ERROR)
                                CODE_409 -> SurveyEvents.ShowSnackBar(DID_SURVEY_ERROR)
                                else -> SurveyEvents.ShowSnackBar(SURVEY_ERROR, RETRY)
                            }
                        )
                    }
                }
            }.onCompletion {
                _events.emit(SurveyEvents.DismissLoading)
            }.launchIn(viewModelScope)

        }

    }

    fun querySurveyDetail() {
        querySurveyDetailUseCase(sid = sId.value).onEach { state ->
            when (state) {
                is BaseState.Success -> {
                    state.data.toSurveyDetailData().apply {
                        _uiState.update { survey ->
                            survey.copy(
                                title = title,
                                reward = reward,
                                headCount = headCount,
                                spendTime = spendTime,
                                responseCount = responseCount,
                                targetInput = targetInput,
                                noticeToPanel = noticeToPanel,
                                surveyDescription = surveyDescription,
                                link = link
                            )
                        }
                    }
                    delay(800)
                    timer.emit(2)
                    delay(800)
                    timer.emit(1)
                    delay(800)
                    _uiState.update { it.copy(isBtnEnable = true) }
                }

                is BaseState.Error -> {
                    _events.emit(
                        if (state.code == CODE_404) SurveyEvents.ShowSnackBar(
                            INVALID_SURVEY_ERROR
                        ) else SurveyEvents.ShowSnackBar(DATA_ERROR)
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun navigateToSurvey() {
        viewModelScope.launch { _events.emit(SurveyEvents.NavigateToSurvey) }
        queryPanelWatermarkInfoUseCase().onEach { state ->
            when (state) {
                is BaseState.Success -> {
                    state.data.panelWatermarkInfo?.toSurveyWatermarkData().apply {
                        _uiState.update { wm ->
                            wm.copy(watermark = this?.watermark ?: "unknown")
                        }
                    }
                }

                is BaseState.Error -> {
                    _events.emit(
                        if (state.code == CODE_404) SurveyEvents.ShowSnackBar(
                            INVALID_SURVEY_ERROR
                        ) else SurveyEvents.ShowSnackBar(DATA_ERROR)
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun navigateToProof() = viewModelScope.launch { _events.emit(SurveyEvents.NavigateToProof) }

    fun navigateToBack() = viewModelScope.launch { _events.emit(SurveyEvents.NavigateToBack) }

    fun navigateToList() {
        viewModelScope.launch { _events.emit(SurveyEvents.NavigateToList) }
    }

    fun setSurveyId(sid: Int) = viewModelScope.launch { sId.emit(sid) }


}

sealed class SurveyEvents {
    data object NavigateToSurvey : SurveyEvents()
    data object NavigateToProof : SurveyEvents()
    data object NavigateToDone : SurveyEvents()
    data object NavigateToList : SurveyEvents()
    data object NavigateToBack : SurveyEvents()
    data object ShowLoading : SurveyEvents()
    data object DismissLoading : SurveyEvents()
    data class ShowToastMsg(val msg: String) : SurveyEvents()
    data class ShowSnackBar(val msg: String, val btn: String? = null) : SurveyEvents()
}

data class SurveyUiState(
    val title: String = "",
    val reward: Int = 0,
    val headCount: Int = 0,
    val spendTime: String = "",
    val responseCount: Int = 0,
    val targetInput: String = "",
    val noticeToPanel: String = "",
    val surveyDescription: String = "",
    val isBtnEnable: Boolean = false,
    val link: String = "",
    val watermark: String = "",
)