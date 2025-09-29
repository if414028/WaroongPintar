package com.nesher.waroongpintar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val id: Long? = null,
    @SerialName("store_id") val storeId: String,
    val status: String? = null,
    @SerialName("current_period_end") val currentPeriodEnd: String? = null,
    val plans: Plan? = null

)
