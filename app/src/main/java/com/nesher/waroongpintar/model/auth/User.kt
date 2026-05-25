package com.nesher.waroongpintar.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String? = null,
    @SerialName("email_verified_at") val emailVerifiedAt: String? = null,
    val status: String? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val roles: List<Role> = emptyList(),
    val permissions: List<JsonElement> = emptyList(),
    val stores: List<Store> = emptyList()
)
