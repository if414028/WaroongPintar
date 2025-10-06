package com.nesher.waroongpintar.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    val id: Long? = null,
    @SerialName("plan_name") val planName: String? = null,
    @SerialName("price_monthly") val priceMonthly: Double? = null
)
