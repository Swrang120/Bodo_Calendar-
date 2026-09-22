package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class DateNote(
  val id: String = UUID.randomUUID().toString(),
  val dateKey: String, // Format: "yyyy-MM-dd"
  val bodoDateDisplay: String = "",
  val title: String,
  val content: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val isCompleted: Boolean = false
)

class UserNotesManager(private val context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("bodo_calendar_user_notes", Context.MODE_PRIVATE)

  private val CHANNEL_ID = "bodo_calendar_reminders_channel"

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Bodo Calendar Date Reminders"
      val descriptionText = "Notifications for user-written date notes and reminders"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableVibration(true)
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        setSound(
          defaultSound,
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
      }
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun getAllNotes(): List<DateNote> {
    val rawJson = prefs.getString("saved_notes_json", "[]") ?: "[]"
    val list = mutableListOf<DateNote>()
    try {
      val array = JSONArray(rawJson)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          DateNote(
            id = obj.optString("id", UUID.randomUUID().toString()),
            dateKey = obj.optString("dateKey", ""),
            bodoDateDisplay = obj.optString("bodoDateDisplay", ""),
            title = obj.optString("title", ""),
            content = obj.optString("content", ""),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
            isCompleted = obj.optBoolean("isCompleted", false)
          )
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return list.sortedByDescending { it.createdAt }
  }

  private fun saveAllNotes(notes: List<DateNote>) {
    val array = JSONArray()
    for (note in notes) {
      val obj = JSONObject().apply {
        put("id", note.id)
        put("dateKey", note.dateKey)
        put("bodoDateDisplay", note.bodoDateDisplay)
        put("title", note.title)
        put("content", note.content)
        put("createdAt", note.createdAt)
        put("isCompleted", note.isCompleted)
      }
      array.put(obj)
    }
    prefs.edit().putString("saved_notes_json", array.toString()).apply()
  }

  fun getNotesForDate(dateKey: String): List<DateNote> {
    return getAllNotes().filter { it.dateKey == dateKey }
  }

  fun getDatesWithNotes(): Set<String> {
    return getAllNotes().map { it.dateKey }.toSet()
  }

  fun addNote(dateKey: String, bodoDateDisplay: String, title: String, content: String): DateNote {
    val newNote = DateNote(
      dateKey = dateKey,
      bodoDateDisplay = bodoDateDisplay,
      title = title.trim(),
      content = content.trim()
    )
    val existing = getAllNotes().toMutableList()
    existing.add(0, newNote)
    saveAllNotes(existing)
    return newNote
  }

  fun deleteNote(id: String) {
    val existing = getAllNotes().filter { it.id != id }
    saveAllNotes(existing)
  }

  fun toggleComplete(id: String) {
    val existing = getAllNotes().map {
      if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
    }
    saveAllNotes(existing)
  }

  // --- Daily Reminder Notification Logic ---

  fun getTodayDateKey(): String {
    val cal = Calendar.getInstance()
    return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cal.time)
  }

  fun hasUnacknowledgedNotesForToday(): List<DateNote> {
    val todayKey = getTodayDateKey()
    val notes = getNotesForDate(todayKey).filter { !it.isCompleted }
    return notes
  }

  fun isReminderShownToday(): Boolean {
    val todayKey = getTodayDateKey()
    val lastShown = prefs.getString("last_shown_reminder_date", "")
    return lastShown == todayKey
  }

  fun markReminderShownToday() {
    val todayKey = getTodayDateKey()
    prefs.edit().putString("last_shown_reminder_date", todayKey).apply()
  }

  fun playReminderSound() {
    try {
      val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val ringtone = RingtoneManager.getRingtone(context, notificationUri)
      if (ringtone != null) {
        ringtone.play()
        return
      }
    } catch (_: Exception) {}

    // Fallback to ToneGenerator if ringtone fails
    try {
      val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
      tone.startTone(ToneGenerator.TONE_PROP_BEEP, 350)
    } catch (_: Exception) {}
  }

  fun showAndroidStatusNotification(notes: List<DateNote>, bodoDateTitle: String) {
    if (notes.isEmpty()) return
    try {
      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val firstNote = notes.first()
      val noteSummary = if (notes.size == 1) {
        firstNote.title
      } else {
        "${firstNote.title} (+${notes.size - 1} more notes)"
      }

      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("🔔 Aapka Aaj Ka Note: $bodoDateTitle")
        .setContentText(noteSummary)
        .setStyle(
          NotificationCompat.BigTextStyle()
            .bigText("Aapka aaj ka reminder note:\n• ${notes.joinToString("\n• ") { it.title + if (it.content.isNotEmpty()) " - " + it.content else "" }}")
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setDefaults(NotificationCompat.DEFAULT_ALL)

      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.notify(1001, builder.build())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
