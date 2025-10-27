package com.example.swipe_assignment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.swipe_assignment.presentation.navigation.Navigation
import com.example.swipe_assignment.ui.theme.Swipe_assignmentTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                openAppNotificationSettings()
            }
        } else {
            val deniedForever = !shouldShowRequestPermissionRationale(
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (deniedForever) {
                openAppNotificationSettings()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val splash = installSplashScreen()
        var keepSplash = true
        splash.setKeepOnScreenCondition { keepSplash }
        lifecycleScope.launch {
            delay(1000)
            keepSplash = false
        }

        ensureNotificationPermission()

        setContent {
            Swipe_assignmentTheme {
                val dark = isSystemInDarkTheme()
                val sysUi = rememberSystemUiController()
                SideEffect {
                    sysUi.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = !dark
                    )
                }
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                ) {
                    Navigation()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.POST_NOTIFICATIONS
                )

                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                openAppNotificationSettings()
            }
        } else {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                openAppNotificationSettings()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            data = Uri.fromParts("package", packageName, null)
        }
        if (intent.resolveActivity(packageManager) == null) {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            })
        } else {
            startActivity(intent)
        }
    }
}
