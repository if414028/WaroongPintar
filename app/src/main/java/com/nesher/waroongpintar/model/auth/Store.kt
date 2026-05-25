package com.nesher.waroongpintar.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val id: Long,
    @SerialName("owner_user_id") val ownerUserId: Long? = null,
    val name: String,
    val code: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val province: String? = null,
    val status: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val pivot: Pivot? = null
) {
    @Serializable
    data class Pivot(
        @SerialName("user_id") val userId: Long? = null,
        @SerialName("store_id") val storeId: Long? = null,
        @SerialName("role_in_store") val roleInStore: String? = null,
        @SerialName("created_at") val createdAt: String? = null,
        @SerialName("updated_at") val updatedAt: String? = null
    )
}
