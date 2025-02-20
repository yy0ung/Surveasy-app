package com.surveasy.surveasy.domain.model

import com.surveasy.surveasy.domain.base.BaseDomainModel

data class PanelWatermark(
    val panelWatermarkInfo: PanelWatermarkInfo?
) : BaseDomainModel

data class PanelWatermarkInfo(
    val name: String?,
    val phoneNumber: String?,
) : BaseDomainModel