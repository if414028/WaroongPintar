package com.nesher.waroongpintar.network

import com.nesher.waroongpintar.BuildConfig

object ApiConfig {
    val baseUrl: String = BuildConfig.API_BASE_URL.trimEnd('/')
}
