package com.nesher.waroongpintar

import android.app.Application
import com.nesher.waroongpintar.network.ApiClient
import com.nesher.waroongpintar.utils.UserConfiguration
import dagger.hilt.android.HiltAndroidApp
import io.ktor.client.HttpClient

@HiltAndroidApp
class App: Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var userConfiguration: UserConfiguration
        private set

    lateinit var apiClient: HttpClient
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        userConfiguration = UserConfiguration(this)
        apiClient = ApiClient.create(userConfiguration)
    }
}
