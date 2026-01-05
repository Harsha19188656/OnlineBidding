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

    fun login(email: String, password: String, loginType: String = "User") {
        _uiState.value = AuthUiState(loading = true, error = null)
        
        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> = RetrofitInstance.api.login(
                    LoginRequest(email = email, password = password)
                )
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true && !responseBody.token.isNullOrBlank()) {
                        val userRole = responseBody.role ?: "user"
                        
                        // Validate role matches selected login type
                        if (loginType == "Admin" && userRole.lowercase() != "admin") {
                            // User selected Admin but credentials are not admin
                            _uiState.value = AuthUiState(
                                loading = false,
                                error = "Access denied. Admin credentials required."
                            )
                            return@launch
                        }
                        
                        if (loginType == "User" && userRole.lowercase() == "admin") {
                            // User selected User but credentials are admin - allow but show message
                            // Or you can block it if you want strict separation
                        }
                        
                        // Login successful
                        _uiState.value = AuthUiState(
                            loading = false,
                            token = responseBody.token,
                            email = responseBody.email ?: email,
                            name = responseBody.name,
                            phone = responseBody.phone,
                            role = userRole,
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
    
    fun googleSignIn(idToken: String) {
        android.util.Log.d("AuthViewModel", "Google Sign-In started, ID Token length: ${idToken.length}")
        _uiState.value = AuthUiState(loading = true, error = null)
        
        viewModelScope.launch {
            try {
                android.util.Log.d("AuthViewModel", "Calling API: google-signin.php")
                val response: Response<com.example.onlinebidding.api.GoogleSignInResponse> = RetrofitInstance.api.googleSignIn(
                    com.example.onlinebidding.api.GoogleSignInRequest(idToken = idToken)
                )
                
                android.util.Log.d("AuthViewModel", "API Response - Code: ${response.code()}, Success: ${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    android.util.Log.d("AuthViewModel", "Response Body: success=${responseBody?.success}, token=${responseBody?.token?.take(10)}..., user=${responseBody?.user?.email}")
                    
                    if (responseBody?.success == true && !responseBody.token.isNullOrBlank()) {
                        val user = responseBody.user
                        val userRole = user?.role ?: "user"
                        
                        android.util.Log.d("AuthViewModel", "Google Sign-In successful! Setting state with token and role: $userRole")
                        
                        // Login successful - state will trigger navigation automatically
                        _uiState.value = AuthUiState(
                            loading = false,
                            token = responseBody.token,
                            email = user?.email,
                            name = user?.name,
                            phone = user?.phone,
                            role = userRole,
                            error = null
                        )
                        
                        android.util.Log.d("AuthViewModel", "State updated. Token set: ${_uiState.value.token != null}")
                    } else {
                        val errorMsg = responseBody?.error ?: responseBody?.message ?: "Google Sign-In failed"
                        android.util.Log.e("AuthViewModel", "Google Sign-In failed: $errorMsg")
                        _uiState.value = AuthUiState(
                            loading = false,
                            error = errorMsg
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthViewModel", "API Error - Code: ${response.code()}, Body: $errorBody")
                    val errorMsg = if (errorBody != null) {
                        try {
                            val errorJson = org.json.JSONObject(errorBody)
                            errorJson.optString("error", "Google Sign-In failed: ${response.code()}")
                        } catch (e: Exception) {
                            "Google Sign-In failed: ${response.code()}"
                        }
                    } else {
                        "Google Sign-In failed: ${response.code()}"
                    }
                    _uiState.value = AuthUiState(
                        loading = false,
                        error = errorMsg
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Exception during Google Sign-In", e)
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
