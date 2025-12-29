package com.example.onlinebidding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinebidding.api.LoginRequest
import com.example.onlinebidding.api.LoginResponse
import com.example.onlinebidding.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

data class AuthUiState(
    val loading: Boolean = false,
    val token: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val role: String? = null, // "admin" or "user"
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        _uiState.value = AuthUiState(loading = true, error = null)
        
        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> = RetrofitInstance.api.login(
                    LoginRequest(email = email, password = password)
                )
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true && !responseBody.token.isNullOrBlank()) {
                        // Login successful
                        _uiState.value = AuthUiState(
                            loading = false,
                            token = responseBody.token,
                            email = responseBody.email ?: email,
                            name = responseBody.name,
                            phone = responseBody.phone,
                            role = responseBody.role ?: "user",
                            error = null
                        )
                    } else {
                        // Login failed - show error from response
                        val errorMsg = responseBody?.error ?: "Login failed"
                        _uiState.value = AuthUiState(
                            loading = false,
                            error = errorMsg
                        )
                    }
                } else {
                    // HTTP error - parse error message
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (errorBody != null) {
                        try {
                            val errorJson = org.json.JSONObject(errorBody)
                            errorJson.optString("error", "Login failed: ${response.code()}")
                        } catch (e: Exception) {
                            if (response.code() == 500) {
                                "Server error. Please check if database is set up correctly."
                            } else {
                                "Login failed: ${response.code()}"
                            }
                        }
                    } else {
                        if (response.code() == 500) {
                            "Server error. Please check if backend is running and database is set up."
                        } else {
                            "Login failed: ${response.code()}"
                        }
                    }
                    _uiState.value = AuthUiState(
                        loading = false,
                        error = errorMsg
                    )
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true -> "Cannot connect to server. Check your internet connection."
                    e.message?.contains("Failed to connect") == true -> "Cannot connect to server. Make sure backend is running."
                    e.message?.contains("timeout") == true -> "Connection timeout. Server may be down."
                    else -> "Error: ${e.message ?: "Unknown error"}"
                }
                _uiState.value = AuthUiState(
                    loading = false,
                    error = errorMsg
                )
                e.printStackTrace()
            }
        }
    }
    
    fun logout() {
        _uiState.value = AuthUiState()
    }
}
