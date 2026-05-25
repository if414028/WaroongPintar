package com.nesher.waroongpintar.utils

import android.content.Context
import androidx.core.content.edit
import com.nesher.waroongpintar.model.auth.Store
import com.nesher.waroongpintar.model.auth.User
import com.nesher.waroongpintar.network.dto.LoginData
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class UserConfiguration(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    val accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)

    val user: User?
        get() = prefs.getString(KEY_USER, null)?.decodeOrNull(User.serializer())

    val roles: List<String>
        get() = prefs.getString(KEY_ROLES, null)
            ?.decodeOrNull(ListSerializer(String.serializer()))
            .orEmpty()

    val permissions: List<JsonElement>
        get() = prefs.getString(KEY_PERMISSIONS, null)
            ?.decodeOrNull(ListSerializer(JsonElement.serializer()))
            .orEmpty()

    val stores: List<Store>
        get() = prefs.getString(KEY_STORES, null)
            ?.decodeOrNull(ListSerializer(Store.serializer()))
            .orEmpty()

    val selectedStoreId: Long?
        get() = if (prefs.contains(KEY_SELECTED_STORE_ID)) {
            prefs.getLong(KEY_SELECTED_STORE_ID, 0L)
        } else {
            null
        }

    fun saveLoginData(data: LoginData) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, data.token)
            putString(KEY_USER, json.encodeToString(User.serializer(), data.user))
            putString(KEY_ROLES, json.encodeToString(ListSerializer(String.serializer()), data.roles))
            putString(
                KEY_PERMISSIONS,
                json.encodeToString(ListSerializer(JsonElement.serializer()), data.permissions)
            )
            putString(KEY_STORES, json.encodeToString(ListSerializer(Store.serializer()), data.stores))
            data.stores.firstOrNull()?.let { putLong(KEY_SELECTED_STORE_ID, it.id) }
        }
    }

    fun setSelectedStoreId(storeId: Long) {
        prefs.edit { putLong(KEY_SELECTED_STORE_ID, storeId) }
    }

    fun isLoggedIn(): Boolean = !accessToken.isNullOrBlank()

    fun clear() {
        prefs.edit { clear() }
    }

    private fun <T> String.decodeOrNull(serializer: kotlinx.serialization.KSerializer<T>): T? =
        runCatching { json.decodeFromString(serializer, this) }.getOrNull()

    private companion object {
        const val PREF_NAME = "user_configuration"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER = "user"
        const val KEY_ROLES = "roles"
        const val KEY_PERMISSIONS = "permissions"
        const val KEY_STORES = "stores"
        const val KEY_SELECTED_STORE_ID = "selected_store_id"
    }
}
