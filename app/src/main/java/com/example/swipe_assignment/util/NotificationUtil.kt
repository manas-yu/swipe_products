package com.example.swipe_assignment.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.swipe_assignment.R

class UploadNotifier(
    private val context: Context,
    private val manager: NotificationManagerCompat = NotificationManagerCompat.from(context),
    @DrawableRes private val smallIcon: Int = R.drawable.upload_progress
) {

    init { ensureChannel() }

    fun startIndeterminate(
        productId: String,
        productName: String,
        message: String? = null,
        contentIntent: PendingIntent? = null,
        cancelAction: NotificationAction? = null
    ) {
        if (!hasPostNotifPermission()) return

        val builder = baseBuilder()
            .setContentTitle("Uploading $productName")
            .setContentText(message ?: "Upload in progress…")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(0, 0, true)
            .setContentIntent(contentIntent)
            .apply { cancelAction?.let { addAction(it.toCompat()) } }

        safeNotify(idFor(productId), builder)
        ensureGroupSummary()
    }

    /** Update a determinate progress (0..100). */
    fun updateProgress(productId: String, productName: String, percent: Int) {
        if (!hasPostNotifPermission()) return

        val p = percent.coerceIn(0, 100)
        val builder = baseBuilder()
            .setContentTitle("Uploading $productName")
            .setContentText("$p%")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, p, false)

        safeNotify(idFor(productId), builder)
    }

    /** Cancel ongoing progress for this product. */
    fun cancelProgress(productId: String) {
        safeCancel(idFor(productId))
    }

    /** Show a success notification and clear any ongoing progress for the same item. */
    fun success(
        productId: String,
        productName: String,
        contentIntent: PendingIntent? = null
    ) {
        if (!hasPostNotifPermission()) return

        val builder = baseBuilder(priority = NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("Product Upload Success")
            .setContentText("$productName has been uploaded successfully")
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        safeNotify(successIdFor(productId), builder)
        cancelProgress(productId)
        ensureGroupSummary()
    }

    /** Show a failure notification (BigText) with optional "Retry" action and clear ongoing progress. */
    fun failure(
        productId: String,
        productName: String,
        error: String,
        retryAction: NotificationAction? = null,
        contentIntent: PendingIntent? = null
    ) {
        if (!hasPostNotifPermission()) return

        val builder = baseBuilder(priority = NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("Product Upload Failed")
            .setContentText("Failed to upload $productName")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Failed to upload $productName: $error")
            )
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .apply { retryAction?.let { addAction(it.toCompat()) } }

        safeNotify(failureIdFor(productId), builder)
        cancelProgress(productId)
        ensureGroupSummary()
    }

    // ----------------- Internals -----------------

    private fun baseBuilder(
        priority: Int = NotificationCompat.PRIORITY_LOW
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setPriority(priority)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setSilent(true) // no sound while updating progress
            .setGroup(GROUP_ID)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                }
            }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Product uploads",
                NotificationManager.IMPORTANCE_LOW // progress should be unobtrusive
            ).apply {
                description = "Shows upload progress and results"
                enableVibration(false)
                setShowBadge(false)
                lightColor = Color.BLUE
            }
            nm.createNotificationChannel(channel)
        }
    }

    private fun ensureGroupSummary() {
        if (!hasPostNotifPermission()) return
        val summary = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle("Uploads")
            .setContentText("Upload activity")
            .setGroup(GROUP_ID)
            .setGroupSummary(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
        try {
            manager.notify(GROUP_SUMMARY_ID, summary)
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException posting group summary", se)
        }
    }

    private fun safeNotify(id: Int, builder: NotificationCompat.Builder) {
        if (!hasPostNotifPermission()) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted. Skipping notify(id=$id).")
            return
        }
        try {
            manager.notify(id, builder.build())
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException posting notification id=$id", se)
        }
    }

    private fun safeCancel(id: Int) {
        if (!hasPostNotifPermission()) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted. Skipping cancel(id=$id).")
            return
        }
        try {
            manager.cancel(id)
        } catch (se: SecurityException) {
            Log.e(TAG, "SecurityException canceling notification id=$id", se)
        }
    }

    private fun hasPostNotifPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /** Stable per-product IDs so updates replace the same notification. */
    private fun idFor(productId: String): Int =
        (ID_PROGRESS_BASE + productId.hashCode()).and(0x7FFFFFFF)

    private fun successIdFor(productId: String): Int =
        (ID_SUCCESS_BASE + productId.hashCode()).and(0x7FFFFFFF)

    private fun failureIdFor(productId: String): Int =
        (ID_FAILURE_BASE + productId.hashCode()).and(0x7FFFFFFF)

    data class NotificationAction(
        val title: String,
        val intent: PendingIntent,
        @DrawableRes val icon: Int = android.R.drawable.ic_menu_revert // safe default
    ) {
        fun toCompat() = NotificationCompat.Action(icon, title, intent)
    }

    companion object {
        private const val TAG = "UploadNotifier"

        private const val CHANNEL_ID = "product_upload_channel"
        private const val GROUP_ID = "product_upload_group"

        private const val GROUP_SUMMARY_ID = 10_000
        private const val ID_PROGRESS_BASE = 20_000
        private const val ID_SUCCESS_BASE  = 30_000
        private const val ID_FAILURE_BASE  = 40_000
    }
}
