package com.example.onlinebidding.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AuthUiState(
    val loading: Boolean = false,
    val token: String? = null,
    val email: String? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // Dummy login (frontend-only)
    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(
            token = "dummy_token",
            email = email
        )
    }
}
