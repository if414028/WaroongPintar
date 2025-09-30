package com.nesher.waroongpintar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreUser(
    @SerialName("is_primary") val isPrimary: Boolean? = null,
    val stores: Store? = null
)
