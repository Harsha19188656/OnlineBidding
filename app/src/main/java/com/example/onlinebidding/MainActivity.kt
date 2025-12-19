package com.example.onlinebidding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.onlinebidding.ui.theme.OnlineBiddingTheme
import com.example.onlinebidding.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppEntry()
        }
    }
}

@Composable
fun AppEntry() {
    OnlineBiddingTheme {
        Surface {
            AppNavHost()
        }
    }
}
