package com.example.swipe_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.swipe_assignment.presentation.navigation.Navigation
import com.example.swipe_assignment.ui.theme.Swipe_assignmentTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val splashscreen = installSplashScreen()
//        var keepSplashScreen = true
//        super.onCreate(savedInstanceState)
//        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
//        lifecycleScope.launch {
//            delay(1000)
//            keepSplashScreen = false
//        }

        setContent {
            Swipe_assignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}