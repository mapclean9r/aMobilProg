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

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "r2d2"
        val name = "Big name"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


// bruk dette for å lage ulike notifications
// eksempl under, bare uten data i guess .-. // lag nye av disse eller ta de inn som param(contentTitle aso..)
// får ikke farger på app-bildet vårt hller ;( no big fix der - tmp notification bilde, noen fix?:D
// skru av eller på darkmode for å teste denne ;S
fun sendNotification(context: Context) {
    val channelId = "r2d2"
    val notificationId = 101

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_notifications_24)
        .setColor(Color.Red.toArgb())
        .setContentTitle("Big event")
        .setContentText("starting today at {time}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_GRANTED){
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

}