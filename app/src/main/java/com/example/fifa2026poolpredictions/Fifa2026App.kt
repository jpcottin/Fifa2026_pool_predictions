package com.example.fifa2026poolpredictions

import android.app.Application
import android.content.Context
import com.example.fifa2026poolpredictions.auth.AuthManager
import com.example.fifa2026poolpredictions.data.local.TokenStore
import com.example.fifa2026poolpredictions.data.network.buildApiService
import com.example.fifa2026poolpredictions.data.repository.Fifa2026Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class Fifa2026App : Application() {
    lateinit var tokenStore: TokenStore
        private set
    lateinit var repository: Fifa2026Repository
        private set
    lateinit var authManager: AuthManager
        private set

    override fun onCreate() {
        super.onCreate()
        tokenStore = TokenStore(this)
        val apiService = buildApiService(
            baseUrlProvider = { getApiBaseUrl() },
            tokenProvider = { runBlocking { tokenStore.token.first() } },
            onUnauthorized = {
                // Clear token store on 401
                runBlocking { tokenStore.clearToken() }
            }
        )
        repository = Fifa2026Repository(apiService)
        authManager = AuthManager(tokenStore, repository)
    }

    fun getApiBaseUrl(): String {
        return getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getString("api_base_url", BuildConfig.API_BASE_URL) ?: BuildConfig.API_BASE_URL
    }

    fun setApiBaseUrl(url: String) {
        val current = getApiBaseUrl()
        if (current != url) {
            getSharedPreferences("app_settings", Context.MODE_PRIVATE)
                .edit()
                .putString("api_base_url", url)
                .apply()
            // Clear session immediately when switching servers
            runBlocking { tokenStore.clearToken() }
        }
    }
}
