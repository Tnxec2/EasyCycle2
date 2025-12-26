package com.kontranik.easycycle.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.kontranik.easycycle.model.Phase
import com.kontranik.easycycle.storage.SettingsService
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

        val settings = SettingsService.loadSettings(context)

        phases.filter { it.key != null }
            .filter{ it.notificateStart == true }
            .distinctBy { it.key }
            .forEach { phase ->
            val notificationId: Int = phase.key!!
            val calendar = getCalendarFromPhase(
                newCycleStartDate,
                phase,
                settings.notificationHour ?: 7,
                settings.notificationMinute ?: 0)
            setNotification(calendar, phase, notificationId)
        }
    }

    private fun getCalendarFromPhase(
        newCycleStartDate: Date,
        phase: Phase,
        notificationHour: Int,
        notificationMinute: Int,
    ): Calendar {

        val calendar = Calendar.getInstance().apply {
            // Temporär: Alarm in 5 Sekunden auslösen
            // add(Calendar.SECOND, 5)

            time = newCycleStartDate
            // Tag des Phasenbeginns hinzufügen
            add(Calendar.DAY_OF_YEAR, phase.from - 1)
            // Alarm z.B. um 9 Uhr morgens auslösen
            set(Calendar.HOUR_OF_DAY, notificationHour)
            set(Calendar.MINUTE, notificationMinute)
            set(Calendar.SECOND, 0)
        }
        return calendar
    }

    private fun setNotification(
        calendar: Calendar,
        phase: Phase,
        notificationId: Int
    ) {

        // Nur planen, wenn der Termin in der Zukunft liegt
        if (calendar.timeInMillis > System.currentTimeMillis()) {
            val desc = if (phase.to != null) "${phase.from} - ${phase.to}. ${phase.desc}"
            else "${phase.from}. ${phase.desc}"

            val alarmIntent = Intent(context, NotificationReceiver::class.java)
                .apply {
                    putExtra(NotificationReceiver.EXTRA_PHASE_NAME, desc)
                    putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
                }

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
                PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    alarmIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE, // Mutable for Android 12+
                )
            } else { // Below Android 12
                PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    alarmIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE, // No FLAG_MUTABLE needed for older versions
                )
            }

            Log.d("", "Scheduled phase notification with id $notificationId at ${calendar.time} for $desc")
            // Alarm planen
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    // Nützlich, um alte Alarme zu entfernen, wenn ein neuer Zyklus beginnt
    fun cancelAllPhaseNotifications(phases: List<Phase>) {
        phases.filter { it.key != null }.forEach { phase ->
            val notificationId: Int = phase.key!!
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

    /**
     * Listet alle geplanten Alarme, die von dieser App erstellt wurden, im Logcat auf.
     * Nützlich für Debugging-Zwecke.
     * @param phases Die Liste aller möglichen Phasen, für die Alarme existieren könnten.
     */
    fun logAllScheduledAlarms(phases: List<Phase>) {
        Log.i("AlarmScheduler", "--- Checking for all scheduled alarms ---")
        var alarmCount = 0

        val allPossiblePhases = phases.filter { it.notificateStart == true }.distinctBy { it.key }

        allPossiblePhases.filter { it.key != null }.forEach { phase ->
            val notificationId: Int = phase.key!!
            val intent = Intent(context, NotificationReceiver::class.java)

            // Wir versuchen, den PendingIntent abzurufen, ohne ihn neu zu erstellen
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            // Wenn der pendingIntent nicht null ist, existiert der Alarm
            if (pendingIntent != null) {
                Log.i("AlarmScheduler", "Found active alarm (ID: $notificationId) for Phase '${phase.desc}'")
                alarmCount++
            }
        }

        if (alarmCount == 0) {
            Log.i("AlarmScheduler", "--- No active alarms found for this app. ---")
        } else {
            Log.i("AlarmScheduler", "--- Found a total of $alarmCount active alarms. ---")
        }
    }

}
