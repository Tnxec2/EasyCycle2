package com.kontranik.easycycle.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kontranik.easycycle.model.Phase
import java.util.Calendar
import java.util.Date

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedulePhaseNotifications(newCycleStartDate: Date, phases: List<Phase>) {
        // Überprüfen, ob die App exakte Alarme planen darf.
        // Dies ist für API 31+ erforderlich.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Hier könnten Sie den Nutzer zu den Einstellungen leiten
                // oder eine alternative, ungenaue Alarmmethode verwenden.
                // Für den Moment wird die Planung einfach übersprungen.
                Log.w("AlarmScheduler", "Cannot schedule exact alarms. Permission not granted.")
                return
            }
        }
        // Zuerst alle alten Alarme abbrechen, um Duplikate zu vermeiden
        cancelAllPhaseNotifications(phases)

        Log.d("AlarmScheduler", "Scheduling phase notifications for new cycle starting on $newCycleStartDate, phases: ${phases.size}")

        phases.distinctBy { it.key }.forEach { phase ->
            val notificationId = phase.key
            val calendar = Calendar.getInstance().apply {
                // Temporär: Alarm in 5 Sekunden auslösen
                // add(Calendar.SECOND, 5)

                time = newCycleStartDate
                // Tag des Phasenbeginns hinzufügen
                add(Calendar.DAY_OF_YEAR, phase.from-1)
                // Alarm z.B. um 9 Uhr morgens auslösen
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            Log.d("AlarmScheduler", "Scheduling phase notification for ${phase.desc} at ${calendar.time}")
            // Nur planen, wenn der Termin in der Zukunft liegt
            if (calendar.timeInMillis > System.currentTimeMillis()) {
                val desc = if (phase.to != null) "${phase.from} - ${phase.to}. ${phase.desc}"
                else "${phase.from}. ${phase.desc}"
                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra(NotificationReceiver.EXTRA_PHASE_NAME, desc)
                    putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                Log.d("AlarmScheduler", "Scheduled phase notification for $desc at ${calendar.time}")
                // Alarm planen
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    // Nützlich, um alte Alarme zu entfernen, wenn ein neuer Zyklus beginnt
    fun cancelAllPhaseNotifications(phases: List<Phase>) {
        phases.forEach { phase ->
            val notificationId = phase.key
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
