package com.example.swipe_assignment.presentation.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swipe_assignment.data.local.entity.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun NotificationItem(
    item: NotificationEntity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Unread dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (!item.isViewed) MaterialTheme.colorScheme.primary else Color.Transparent)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = item.productName,
                    fontSize = 16.sp,
                    fontWeight = if (!item.isViewed) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle row: product type · time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = item.productType,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = " · ",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = item.timestamp.toRelativeTime(),
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Status chip
            AssistChip(
                onClick = { /* no-op */ },
                label = {
                    Text(
                        text = item.status.name, // e.g., "Pending" / "Completed"
                        fontStyle = FontStyle.Italic
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = item.status.color.copy(alpha = 0.12f),
                    labelColor = item.status.color
                ),
                border = AssistChipDefaults.assistChipBorder(enabled = false)
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            thickness = 0.7.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    }
}

private fun Long.toRelativeTime(now: Long = System.currentTimeMillis()): String {
    val diff = now - this
    if (diff < 0L) return formatDay(this) // future-safe; show date

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days == 1L -> "Yesterday"
        days < 7L -> "${days}d"
        else -> formatDay(this)
    }
}

private fun formatDay(ts: Long): String {
    val fmt = SimpleDateFormat("MMM d", Locale.getDefault()) // e.g., "Oct 12"
    return fmt.format(ts)
}
