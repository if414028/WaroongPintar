package com.nesher.waroongpintar.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore("login_prefs")

    private val dataStore = context.dataStore

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_REMEMBER = booleanPreferencesKey("remember")
    }

    suspend fun saveLogin(email: String, password: String, remember: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_EMAIL] = email
            prefs[KEY_PASSWORD] = if (remember) password else "" // kosongkan jika tidak remember
            prefs[KEY_REMEMBER] = remember
        }
    }

    val getLogin: Flow<Triple<String, String, Boolean>> = dataStore.data
        .map { prefs ->
            val email = prefs[KEY_EMAIL] ?: ""
            val password = prefs[KEY_PASSWORD] ?: ""
            val remember = prefs[KEY_REMEMBER] ?: false
            Triple(email, password, remember)
        }

    suspend fun clearLogin() {
        dataStore.edit { it.clear() }
    }

}