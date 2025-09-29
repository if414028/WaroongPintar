package com.nesher.waroongpintar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val id: String,
    @SerialName("plan_name") val planName: String? = null,
    @SerialName("price_monthly") val priceMonthly: Long? = null
)
