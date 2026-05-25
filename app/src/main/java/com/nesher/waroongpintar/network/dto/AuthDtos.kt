package com.nesher.waroongpintar.network.dto

import com.nesher.waroongpintar.model.auth.Store
import com.nesher.waroongpintar.model.auth.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: LoginData? = null
) {
    val resolvedToken: String?
        get() = data?.token
}

@Serializable
data class LoginData(
    val token: String,
    val user: User,
    val roles: List<String> = emptyList(),
    val permissions: List<JsonElement> = emptyList(),
    val stores: List<Store> = emptyList()
)

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String
)

@Serializable
data class PasswordResetRequest(
    val email: String,
    @SerialName("redirect_url") val redirectUrl: String
)

@Serializable
data class UpdatePasswordRequest(
    val password: String
)
