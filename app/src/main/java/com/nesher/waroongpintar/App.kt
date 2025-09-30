package com.nesher.waroongpintar

import android.app.Application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class App: Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var supabase: SupabaseClient

    override fun onCreate() {
        super.onCreate()
        instance = this

        supabase = createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest)
        }
    }
}