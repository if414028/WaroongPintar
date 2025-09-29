package com.nesher.waroongpintar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val id: String,
    @SerialName("store_id") val storeId: String,
    val status: String? = null, // "active" / "paused" / "canceled"
    @SerialName("next_billing_at") val nextBillingAt: String? = null, // ISO8601 string
    val plans: Plan? = null
)
