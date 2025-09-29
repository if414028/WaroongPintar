package com.nesher.waroongpintar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    @SerialName("id") val id: String,
    @SerialName("store_name") val storeName: String? = null,
    @SerialName("store_address") val storeAddress: String? = null
)