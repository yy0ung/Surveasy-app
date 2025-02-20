package com.surveasy.surveasy.presentation.main.survey.mapper

import com.surveasy.surveasy.domain.model.PanelWatermarkInfo
import com.surveasy.surveasy.presentation.main.survey.model.UiSurveyWatermarkData

fun PanelWatermarkInfo.toSurveyWatermarkData(): UiSurveyWatermarkData = UiSurveyWatermarkData(
    watermark = "@$name$phoneNumber"
)