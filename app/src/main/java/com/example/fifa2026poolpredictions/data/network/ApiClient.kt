package com.example.fifa2026poolpredictions.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

fun buildApiService(
    baseUrlProvider: () -> String,
    tokenProvider: () -> String?,
    onUnauthorized: () -> Unit = {}
): ApiService {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val newBaseUrl = baseUrlProvider().toHttpUrl()
            val originalRequest = chain.request()
            val newUrl = originalRequest.url.newBuilder()
                .scheme(newBaseUrl.scheme)
                .host(newBaseUrl.host)
                .port(newBaseUrl.port)
                .build()

            val token = tokenProvider()
            val reqBuilder = originalRequest.newBuilder().url(newUrl)
            if (token != null) {
                reqBuilder.addHeader("Authorization", "Bearer $token")
            }
            val response = chain.proceed(reqBuilder.build())
            
            if (response.code == 401) {
                onUnauthorized()
            }
            
            response
        }
        .addInterceptor(logging)
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrlProvider())
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)
}
