package com.example.swipe_assignment.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.swipe_assignment.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_UPLOAD_PROGRESS = "upload_progress"
        const val CHANNEL_UPLOAD_RESULT = "upload_result"

        private const val ID_PROGRESS = 1001
        private const val ID_RESULT = 1002
    }

    // ---- Public API ---------------------------------------------------------

    fun showUploadProgressNotification(productName: String) {
        val notif = NotificationCompat.Builder(context, CHANNEL_UPLOAD_PROGRESS)
            .setSmallIcon(R.drawable.upload_progress)
            .setContentTitle("Uploading: $productName")
            .setContentText("In progress…")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(0, 0, true)
            .build()
        safeNotify(ID_PROGRESS, notif)
    }

    fun hideProgressNotification() {
        safeCancel(ID_PROGRESS)
    }

    fun showUploadSuccessNotification(productName: String) {
        val notif = NotificationCompat.Builder(context, CHANNEL_UPLOAD_RESULT)
            .setSmallIcon(R.drawable.upload_success)
            .setContentTitle("Uploaded")
            .setContentText("$productName uploaded successfully")
            .setAutoCancel(true)
            .build()
        safeNotify(ID_RESULT, notif)
    }

    fun showUploadFailureNotification(productName: String, reason: String?) {
        val notif = NotificationCompat.Builder(context, CHANNEL_UPLOAD_RESULT)
            .setSmallIcon(R.drawable.upload_failed)
            .setContentTitle("Upload failed")
            .setContentText("$productName: ${reason ?: "Unknown error"}")
            .setAutoCancel(true)
            .build()
        safeNotify(ID_RESULT, notif)
    }

    // ---- Safety wrappers ----------------------------------------------------

    private fun safeNotify(id: Int, notification: android.app.Notification) {
        if (!canPostNotifications()) return
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (se: SecurityException) {
            // Permission revoked at runtime or OEM quirk; ignore gracefully
            android.util.Log.w("NotificationHelper", "notify() blocked by SecurityException", se)
        }
    }

    private fun safeCancel(id: Int) {
        // cancel doesn't require permission, but wrap for parity
        try {
            NotificationManagerCompat.from(context).cancel(id)
        } catch (se: SecurityException) {
            android.util.Log.w("NotificationHelper", "cancel() blocked by SecurityException", se)
        }
    }

    // ---- Permission / settings checks --------------------------------------

    private fun canPostNotifications(): Boolean {
        // 1) Are app notifications enabled in system settings?
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false

        // 2) On Android 13+ we also need POST_NOTIFICATIONS runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return true
    }
}
