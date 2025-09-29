package com.nesher.waroongpintar.network

import com.nesher.waroongpintar.model.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

/**
 * Auth repository untuk Supabase v3.
 * Gunakan dari ViewModel dengan coroutine (viewModelScope).
 */
class AuthRepository(private val client: SupabaseClient) {

    /** Login email + password (v3) */
    suspend fun signIn(email: String, password: String): Result<UserSession?> = runCatching {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }                                                // signInWith(Email) + config lambda (v3) :contentReference[oaicite:2]{index=2}
        client.auth.currentSessionOrNull()
    }

    /** Sign up email + password */
    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        Unit
    }

    /**
     * Kirim email reset password.
     * NOTE: param di v3 adalah `redirectUrl` (bukan redirectTo).
     * Pastikan redirectUrl terdaftar di Auth → Settings → Redirect URLs.
     */
    suspend fun sendPasswordReset(email: String, redirectUrl: String): Result<Unit> = runCatching {
        client.auth.resetPasswordForEmail(
            email = email,
            redirectUrl = redirectUrl
        )
    }

    /** Update password untuk user yang sudah login (step 2 setelah deep link) */
    suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching {
        client.auth.updateUser {
            password = newPassword
        }
    }

    /** Logout */
    suspend fun signOut(): Result<Unit> = runCatching {
        client.auth.signOut()   // revoke & hapus session lokal
        Unit
    }

    /** Fetch Profile data **/
    suspend fun fetchMyProfileWithStore(): Result<Profiles> = runCatching {
        val user = client.auth.currentUserOrNull() ?: error("Belum login")

        client.postgrest["profiles"].select(
            columns = Columns.raw(
                """
                id,user_name,user_fullname,email,phone,is_active,
                store_users(
                    is_primary,
                    stores(id,store_name,store_address)
                )
            """.trimIndent()
            )
        ) {
            filter { eq("id", user.id) }
        }.decodeSingle<Profiles>()
    }

    /** Helpers */
    fun currentSession(): UserSession? = client.auth.currentSessionOrNull()
    fun currentUser(): UserInfo? = client.auth.currentUserOrNull()
    fun isLoggedIn(): Boolean = currentSession() != null

}