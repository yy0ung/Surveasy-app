package com.surveasy.surveasy.domain.usecase

import com.surveasy.surveasy.domain.base.BaseState
import com.surveasy.surveasy.domain.model.PanelWatermark
import com.surveasy.surveasy.domain.repository.PanelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QueryPanelWatermarkInfoUseCase @Inject constructor(
    private val repository: PanelRepository
) {
    operator fun invoke(): Flow<BaseState<PanelWatermark>> =
        repository.queryPanelWatermarkInfo()
}
