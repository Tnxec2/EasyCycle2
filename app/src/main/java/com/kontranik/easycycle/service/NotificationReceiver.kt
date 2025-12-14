package com.kontranik.easycycle.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val phaseName = intent.getStringExtra(EXTRA_PHASE_NAME) ?: "EasyCycle"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)

        // Intent, der ausgeführt wird, wenn der Benutzer auf die Benachrichtigung tippt
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Benachrichtigung erstellen
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ersetzen Sie dies mit Ihrem App-Icon
            .setContentTitle(context.getString(R.string.phase_starts_today_title)) // z.B. "Phase beginnt heute"
            .setContentText(phaseName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Kanal für Android 8.0+ erstellen
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Cycle Phases", // Kanalname, der in den App-Einstellungen angezeigt wird
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for the start of cycle phases"
        }
        notificationManager.createNotificationChannel(channel)
        Log.d("NotificationReceiver", "onReceive: $notificationId, $phaseName")
        // Benachrichtigung anzeigen
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "EASYCYCLE_PHASE_CHANNEL"
        const val EXTRA_PHASE_NAME = "EXTRA_PHASE_NAME"
        const val EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID"
    }
}
