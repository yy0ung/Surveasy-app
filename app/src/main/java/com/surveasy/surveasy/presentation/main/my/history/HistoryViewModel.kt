package com.surveasy.surveasy.presentation.main.my.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surveasy.surveasy.domain.base.BaseState
import com.surveasy.surveasy.domain.usecase.ListHistoryUseCase
import com.surveasy.surveasy.presentation.main.my.history.mapper.toUiHistorySurveyData
import com.surveasy.surveasy.presentation.main.my.history.model.UiHistorySurveyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val listHistoryUseCase: ListHistoryUseCase,
) : ViewModel() {
    private val sid = MutableStateFlow(-1)

    private val _mainUiState = MutableStateFlow(HistoryUiState())
    val mainUiState: StateFlow<HistoryUiState> = _mainUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(HistoryDetailUiState())
    val detailUiState: StateFlow<HistoryDetailUiState> = _detailUiState.asStateFlow()

    private val _events = MutableSharedFlow<HistoryEvents>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<HistoryEvents> = _events.asSharedFlow()

    fun listHistory(isBefore: Boolean) {
        val type = if (isBefore) BEFORE else AFTER
        listHistoryUseCase(type).onEach { state ->
            when (state) {
                is BaseState.Success -> {
                    state.data.let { history ->
                        _mainUiState.update {
                            val data =
                                history.responseList.map { list -> list.toUiHistorySurveyData() }
                            it.copy(
                                isBefore = isBefore,
                                list = data
                            )
                        }
                    }
                }

                else -> Log.d("TEST", "failed")
            }
        }.launchIn(viewModelScope)

    }

    fun getHistoryDetail() {
        mainUiState.value.list.find { it.id == sid.value }.apply {
            Log.d("TEST", "$this")
            this ?: return@apply
            _detailUiState.update { state ->
                state.copy(
                    title = title,
                    reward = reward,
                    imgUrl = imgUrl,
                    createdAt = createdAt,
                    sentAt = sentAt
                )
            }
        }


    }

    fun navigateToDetail(id: Int) {
        viewModelScope.launch {
            _events.emit(HistoryEvents.NavigateToDetail)
            sid.emit(id)
        }
    }

    fun navigateToEdit() {
        viewModelScope.launch { _events.emit(HistoryEvents.NavigateToEdit) }
    }

    companion object {
        const val BEFORE = "before"
        const val AFTER = "after"
    }

}

sealed class HistoryEvents {
    data object NavigateToDetail : HistoryEvents()
    data object NavigateToEdit : HistoryEvents()
}

data class HistoryUiState(
    val reward: Int = 0,
    val account: String = "",
    val owner: String = "",
    val pageNum: Int = 0,
    val pageSize: Int = 0,
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val isBefore: Boolean = true,
    val list: List<UiHistorySurveyData> = emptyList()
)

data class HistoryDetailUiState(
    val title: String = "",
    val reward: Int = 0,
    val imgUrl: String = "",
    val createdAt: String = "",
    val sentAt: String? = null,
)