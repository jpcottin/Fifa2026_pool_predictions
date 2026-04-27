package com.example.fifa2026poolpredictions.auth

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()
    data class LoggedIn(
        val userId: String,
        val role: String,
        val name: String?,
        val email: String?,
        val image: String?
    ) : AuthState() {
        val isAdmin: Boolean get() = role == "ADMIN"
    }
}
