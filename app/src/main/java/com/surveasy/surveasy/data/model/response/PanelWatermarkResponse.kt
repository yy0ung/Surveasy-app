package com.surveasy.surveasy.data.model.response

import com.google.gson.annotations.SerializedName
import com.surveasy.surveasy.data.mapper.DomainMapper
import com.surveasy.surveasy.data.model.BaseDataModel
import com.surveasy.surveasy.data.model.response.PanelWatermarkInfoResponse.Companion.toDomainModel
import com.surveasy.surveasy.domain.model.PanelWatermark
import com.surveasy.surveasy.domain.model.PanelWatermarkInfo

data class PanelWatermarkResponse(
    @SerializedName("panelWatermarkInfo")
    val panelWatermarkInfoResponse: PanelWatermarkInfoResponse?
) : BaseDataModel {
    companion object : DomainMapper<PanelWatermarkResponse, PanelWatermark> {
        override fun PanelWatermarkResponse.toDomainModel(): PanelWatermark = PanelWatermark(
            panelWatermarkInfo = panelWatermarkInfoResponse?.toDomainModel()
        )
    }
}

data class PanelWatermarkInfoResponse(
    val name: String?,
    val phoneNumber: String?,
) : BaseDataModel {
    companion object : DomainMapper<PanelWatermarkInfoResponse, PanelWatermarkInfo> {
        override fun PanelWatermarkInfoResponse.toDomainModel(): PanelWatermarkInfo =
            PanelWatermarkInfo(
                name = name,
                phoneNumber = phoneNumber
            )
    }
}