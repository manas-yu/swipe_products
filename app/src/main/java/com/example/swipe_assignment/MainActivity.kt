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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.swipe_assignment.presentation.common.NotificationPermissionDialog
import com.example.swipe_assignment.presentation.navigation.Navigation
import com.example.swipe_assignment.presentation.theme.Swipe_assignmentTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var showNotifDialog by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    private val notifPermLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            showNotifDialog = false
            if (granted) {
                if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    openAppNotificationSettings()
                }
            } else {
                val deniedForever = !shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                )
                if (deniedForever) openAppNotificationSettings()
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
                    sysUi.setSystemBarsColor(Color.Transparent, darkIcons = !dark)
                }

                if (showNotifDialog) {
                    NotificationPermissionDialog(
                        onDismiss = { showNotifDialog = false },
                        onConfirm = {
                            showNotifDialog = false
                            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
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
        val notifManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.POST_NOTIFICATIONS
                    )
                ) {
                    // Show rationale dialog instead of launching directly
                    showNotifDialog = true
                } else {
                    notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else if (!notifManager.areNotificationsEnabled()) {
                openAppNotificationSettings()
            }
        } else if (!notifManager.areNotificationsEnabled()) {
            openAppNotificationSettings()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAppNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            data = Uri.fromParts("package", packageName, null)
        }
        val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent.resolveActivity(packageManager)?.let { intent } ?: fallback)
    }
}
