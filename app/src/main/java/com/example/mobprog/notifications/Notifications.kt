package com.example.mobprog.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mobprog.R

object NotificationChannels {
    const val EVENT_CHANNEL = "event_channel"
    const val FRIEND_REQUEST_CHANNEL = "friend_request_channel"
    const val MESSAGE_CHANNEL = "message_channel"
}

object NotificationIds {
    private var currentId = 0
    fun nextId(): Int = ++currentId
    
    const val EVENT_BASE = 1000
    const val FRIEND_REQUEST_BASE = 2000
    const val MESSAGE_BASE = 3000
}

fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channels = listOf(
            NotificationChannel(
                NotificationChannels.EVENT_CHANNEL,
                "Events",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for upcoming events"
            },
            NotificationChannel(
                NotificationChannels.FRIEND_REQUEST_CHANNEL,
                "Friend Requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new friend requests"
            },
            NotificationChannel(
                NotificationChannels.MESSAGE_CHANNEL,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new messages"
            }
        )

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }
}

fun sendEventNotification(context: Context, eventTitle: String, eventTime: String) {
    val builder = NotificationCompat.Builder(context, NotificationChannels.EVENT_CHANNEL)
        .setSmallIcon(R.drawable.baseline_notifications_24)
        .setColor(Color.Blue.toArgb())
        .setContentTitle("Event Today")
        .setContentText("$eventTitle starts at $eventTime")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    showNotification(context, builder, NotificationIds.EVENT_BASE + NotificationIds.nextId())
}

fun sendFriendRequestNotification(context: Context, fromUser: String) {
    val builder = NotificationCompat.Builder(context, NotificationChannels.FRIEND_REQUEST_CHANNEL)
        .setSmallIcon(R.drawable.baseline_person_24)
        .setColor(Color.Green.toArgb())
        .setContentTitle("New Friend Request")
        .setContentText("$fromUser wants to be your friend")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    showNotification(context, builder, NotificationIds.FRIEND_REQUEST_BASE + NotificationIds.nextId())
}

fun sendMessageNotification(context: Context, fromUser: String, messagePreview: String) {
    val builder = NotificationCompat.Builder(context, NotificationChannels.MESSAGE_CHANNEL)
        .setSmallIcon(R.drawable.baseline_contacts_24)
        .setColor(Color.Magenta.toArgb())
        .setContentTitle("New Message from $fromUser")
        .setContentText(messagePreview)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    showNotification(context, builder, NotificationIds.MESSAGE_BASE + NotificationIds.nextId())
}

private fun showNotification(
    context: Context,
    builder: NotificationCompat.Builder,
    notificationId: Int
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}
