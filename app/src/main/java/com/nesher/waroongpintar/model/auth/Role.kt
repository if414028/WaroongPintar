package com.nesher.waroongpintar.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: Long,
    val name: String,
    @SerialName("guard_name") val guardName: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val pivot: Pivot? = null
) {
    @Serializable
    data class Pivot(
        @SerialName("model_type") val modelType: String? = null,
        @SerialName("model_id") val modelId: Long? = null,
        @SerialName("role_id") val roleId: Long? = null
    )
}
