package com.example.onlinebidding.utils

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

/**
 * Get Google Sign-In Client
 */
fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    // IMPORTANT: Replace "YOUR_WEB_CLIENT_ID" with your actual Web Client ID from Google Cloud Console
    // Get it from: https://console.cloud.google.com/apis/credentials
    val webClientId = "YOUR_WEB_CLIENT_ID"
    
    // Check if Web Client ID is configured
    if (webClientId == "YOUR_WEB_CLIENT_ID" || webClientId.isBlank()) {
        android.util.Log.e("GoogleSignIn", "⚠️ Web Client ID not configured! Please set it in GoogleSignInHelper.kt")
    }
    
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(webClientId)
        .build()
    
    return GoogleSignIn.getClient(context, gso)
}

/**
 * Composable function to handle Google Sign-In
 */
@Composable
fun rememberGoogleSignInLauncher(
    onSignInSuccess: (String) -> Unit, // idToken callback
    onSignInError: (String) -> Unit = {} // error callback
): androidx.activity.result.ActivityResultLauncher<android.content.Intent> {
    val context = LocalContext.current
    
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Check if result is OK
        if (result.resultCode == androidx.activity.ComponentActivity.RESULT_OK) {
            try {
                val data = result.data
                if (data != null) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task, onSignInSuccess, onSignInError)
                } else {
                    val errorMessage = "No data received from Google Sign-In"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    onSignInError(errorMessage)
                }
            } catch (e: ApiException) {
                val errorMessage = "Google Sign-In failed: ${e.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                onSignInError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                onSignInError(errorMessage)
            }
        } else {
            // User cancelled or result was not OK
            if (result.resultCode != androidx.activity.ComponentActivity.RESULT_CANCELED) {
                val errorMessage = "Google Sign-In was cancelled"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                onSignInError(errorMessage)
            }
        }
    }
}

/**
 * Handle Google Sign-In result
 */
private fun handleSignInResult(
    completedTask: Task<GoogleSignInAccount>,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        val idToken = account.idToken
        
        if (idToken != null && idToken.isNotBlank()) {
            // Successfully got ID token - proceed with sign-in
            android.util.Log.d("GoogleSignIn", "ID Token received, length: ${idToken.length}")
            onSuccess(idToken)
        } else {
            // ID token is null or empty - Web Client ID might not be configured
            val errorMessage = "Failed to get ID token. Please configure Web Client ID in GoogleSignInHelper.kt. See GOOGLE_SIGNIN_SETUP.md for instructions."
            android.util.Log.e("GoogleSignIn", errorMessage)
            onError(errorMessage)
        }
    } catch (e: ApiException) {
        val errorMessage = when (e.statusCode) {
            10 -> "Developer error - Web Client ID not configured. Please set it in GoogleSignInHelper.kt. See GOOGLE_SIGNIN_SETUP.md"
            12501 -> "Sign-in was cancelled by user"
            7 -> "Network error - please check your internet connection"
            8 -> "Internal error - please try again"
            else -> "Sign-in failed: ${e.message} (Error code: ${e.statusCode})"
        }
        android.util.Log.e("GoogleSignIn", errorMessage, e)
        onError(errorMessage)
    } catch (e: Exception) {
        val errorMessage = "Unexpected error: ${e.message}"
        android.util.Log.e("GoogleSignIn", errorMessage, e)
        onError(errorMessage)
    }
}

