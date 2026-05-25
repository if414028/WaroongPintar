package com.nesher.waroongpintar.network

import com.nesher.waroongpintar.App
import com.nesher.waroongpintar.data.model.Profile
import com.nesher.waroongpintar.data.model.Subscription
import com.nesher.waroongpintar.network.dto.LoginRequest
import com.nesher.waroongpintar.network.dto.LoginResponse
import com.nesher.waroongpintar.network.dto.PasswordResetRequest
import com.nesher.waroongpintar.network.dto.SignUpRequest
import com.nesher.waroongpintar.network.dto.UpdatePasswordRequest
import com.nesher.waroongpintar.utils.UserConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Auth repository untuk REST API.
 * Gunakan dari ViewModel dengan coroutine (viewModelScope).
 */
class AuthRepository(
    private val client: HttpClient = App.instance.apiClient,
    private val userConfiguration: UserConfiguration = App.instance.userConfiguration
) {

    /** Login email + password */
    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        val response = client.post("/api/login") {
            setBody(LoginRequest(email = email, password = password))
        }.body<LoginResponse>()

        val data = response.data ?: error(response.message ?: "Login gagal")
        if (!response.success || data.token.isBlank()) {
            error(response.message ?: "Login gagal")
        }

        userConfiguration.saveLoginData(data)
        Unit
    }

    /** Sign up email + password */
    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        client.post("/auth/register") {
            setBody(SignUpRequest(email = email, password = password))
        }
        Unit
    }

    /** Kirim email reset password. */
    suspend fun sendPasswordReset(email: String, redirectUrl: String): Result<Unit> = runCatching {
        client.post("/auth/password/reset") {
            setBody(PasswordResetRequest(email = email, redirectUrl = redirectUrl))
        }
        Unit
    }

    /** Update password untuk user yang sudah login (step 2 setelah deep link) */
    suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching {
        client.patch("/auth/password") {
            setBody(UpdatePasswordRequest(password = newPassword))
        }
        Unit
    }

    /** Logout */
    suspend fun signOut(): Result<Unit> = runCatching {
        runCatching {
            client.post("/auth/logout")
        }
        userConfiguration.clear()
        Unit
    }

    /** Fetch Profile data **/
    suspend fun fetchMyProfileWithStore(): Result<Profile> = runCatching {
        client.get("/auth/me").body<Profile>()
    }

    suspend fun fetchActiveSubscriptionForStore(storeId: String): Result<Subscription?> = runCatching {
        client.get("/stores/$storeId/subscriptions/active").body<Subscription?>()
    }

    /** Helpers */
    fun isLoggedIn(): Boolean = userConfiguration.isLoggedIn()

    suspend fun validateSession(): Result<Unit> = runCatching {
        fetchMyProfileWithStore().getOrThrow()
        Unit
    }.onFailure {
        userConfiguration.clear()
    }

}
