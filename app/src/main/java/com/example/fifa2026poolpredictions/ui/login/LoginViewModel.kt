package com.example.fifa2026poolpredictions.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifa2026poolpredictions.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state

    fun signIn(context: Context) {
        viewModelScope.launch {
            _state.value = LoginUiState.Loading
            authManager.signIn(context).fold(
                onSuccess = { _state.value = LoginUiState.Success },
                onFailure = { _state.value = LoginUiState.Error(it.message ?: "Sign-in failed") }
            )
        }
    }
}
