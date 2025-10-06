package com.nesher.waroongpintar.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    @SerialName("user_name") val userName: String? = null,
    @SerialName("user_fullname") val userFullname: String? = null,
    val email: String? = null,
    val phone: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("store_users") val storeUsers: List<StoreUser> = emptyList()
) {
    val primaryStore: Store?
        get() = storeUsers.firstOrNull { it.isPrimary == true }?.stores
}
