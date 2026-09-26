package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppUpdateInfo
import com.example.data.AppUpdateManager
import com.example.data.BodoDate
import com.example.data.BodoHistoryDatabase
import com.example.data.BodoMonth
import com.example.data.BodoSolarCalendar
import com.example.data.CulturalNewsItem
import com.example.data.DateNote
import com.example.data.SupabaseManager
import com.example.data.UserNotesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CalendarUiState(
  val currentSystemDate: Calendar = Calendar.getInstance(),
  val displayedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
  val displayedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1, // 1..12
  val selectedDate: BodoDate = BodoSolarCalendar.convertToBodoDate(Calendar.getInstance()),
  val monthDays: List<BodoDate> = emptyList(),
  val liveMessages: List<CulturalNewsItem> = emptyList(),
  val todayBodoDate: BodoDate = BodoSolarCalendar.convertToBodoDate(Calendar.getInstance()),
  val tomorrowBodoDate: BodoDate = BodoSolarCalendar.convertToBodoDate(Calendar.getInstance().apply { add(Calendar.DATE, 1) }),
  val dayAfterBodoDate: BodoDate = BodoSolarCalendar.convertToBodoDate(Calendar.getInstance().apply { add(Calendar.DATE, 2) }),
  val todayEvents: List<CulturalNewsItem> = emptyList(),
  val tomorrowEvents: List<CulturalNewsItem> = emptyList(),
  val dayAfterEvents: List<CulturalNewsItem> = emptyList(),
  val liveTickerIndex: Int = 0,
  val currentRunningMonthBodo: BodoMonth = BodoSolarCalendar.convertToBodoDate(Calendar.getInstance()).bodoMonth,
  val currentRunningMonthEng: String = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().time),
  val showMonthExplanationDialog: Boolean = false,
  val showAdminDialog: Boolean = false,
  val showAppDownloadDialog: Boolean = false,
  val adminPublishStatus: String? = null,
  val availableUpdate: AppUpdateInfo? = null,
  val upToDateNotice: String? = null,
  // User Notes & Reminders
  val savedNotes: List<DateNote> = emptyList(),
  val datesWithNotes: Set<String> = emptySet(),
  val activeNoteDialogDateKey: String? = null,
  val activeNoteDialogDisplay: String = "",
  val todayReminderAlertNotes: List<DateNote>? = null
)

class BodoCalendarViewModel(application: Application) : AndroidViewModel(application) {

  private val supabaseManager = SupabaseManager(application)
  private val userNotesManager = UserNotesManager(application)
  private val updatePrefs = application.getSharedPreferences("app_update_prefs", Context.MODE_PRIVATE)

  private val _uiState = MutableStateFlow(CalendarUiState())
  val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  init {
    refreshNotes()
    refreshCalendarData()
    startMidnightAutoRolloverEngine()
    startLiveTickerRotation()
    syncCloudAnnouncements()
    checkTodayReminders(forceAlert = false)
    // Automatically check GitHub for a newer APK when the app starts.
    checkForAppUpdates(isManual = false)
    startAutomaticUpdateChecker()
  }

  // --- Date Notes & Reminders Management ---

  fun refreshNotes() {
    val allNotes = userNotesManager.getAllNotes()
    val datesWithNotes = userNotesManager.getDatesWithNotes()
    _uiState.value = _uiState.value.copy(
      savedNotes = allNotes,
      datesWithNotes = datesWithNotes
    )
  }

  fun checkTodayReminders(forceAlert: Boolean = false) {
    val unacknowledgedNotes = userNotesManager.hasUnacknowledgedNotesForToday()
    if (unacknowledgedNotes.isNotEmpty()) {
      if (forceAlert || !userNotesManager.isReminderShownToday()) {
        userNotesManager.playReminderSound()
        val todayBodo = _uiState.value.todayBodoDate
        val dateDisplay = "${todayBodo.bodoDay} ${todayBodo.bodoMonth.bodoName} • ${todayBodo.gregorianDay}/${todayBodo.gregorianMonth}/${todayBodo.gregorianYear}"
        userNotesManager.showAndroidStatusNotification(unacknowledgedNotes, dateDisplay)
        _uiState.value = _uiState.value.copy(
          todayReminderAlertNotes = unacknowledgedNotes
        )
      }
    }
  }

  fun acknowledgeTodayReminder() {
    userNotesManager.markReminderShownToday()
    _uiState.value = _uiState.value.copy(todayReminderAlertNotes = null)
  }

  fun openDateNotesDialog(dateKey: String, dateDisplayTitle: String) {
    _uiState.value = _uiState.value.copy(
      activeNoteDialogDateKey = dateKey,
      activeNoteDialogDisplay = dateDisplayTitle
    )
  }

  fun closeDateNotesDialog() {
    _uiState.value = _uiState.value.copy(activeNoteDialogDateKey = null)
  }

  fun addNoteForDate(dateKey: String, title: String, content: String) {
    val bodoDisplay = _uiState.value.activeNoteDialogDisplay
    userNotesManager.addNote(dateKey, bodoDisplay, title, content)
    refreshNotes()
    // If note is for today, check reminders
    val todayKey = userNotesManager.getTodayDateKey()
    if (dateKey == todayKey) {
      checkTodayReminders(forceAlert = true)
    }
  }

  fun deleteNote(id: String) {
    userNotesManager.deleteNote(id)
    refreshNotes()
  }

  fun toggleNoteComplete(id: String) {
    userNotesManager.toggleComplete(id)
    refreshNotes()
  }

  // --- App Update Management ---

  /**
   * Only show update dialog when remote version is actually higher than the current build!
   * Respects user's dismissal so it does not annoy on every app open.
   */
  fun checkForAppUpdates(isManual: Boolean = false) {
    viewModelScope.launch {
      val currentVersionCode = BuildConfig.VERSION_CODE
      val update = AppUpdateManager.checkForUpdates(currentVersionCode)
      if (update != null && update.versionCode > currentVersionCode) {
        val lastDismissed = updatePrefs.getInt("dismissed_version_code", 0)
        if (isManual || update.versionCode > lastDismissed) {
          _uiState.value = _uiState.value.copy(availableUpdate = update)
        }
      } else if (isManual) {
        _uiState.value = _uiState.value.copy(
          upToDateNotice = "✓ Aapka Bodo Calendar app pehle se hi latest version (v${BuildConfig.VERSION_NAME}) par hai! Naye update ki zaroorat nahi hai."
        )
        delay(3500L)
        _uiState.value = _uiState.value.copy(upToDateNotice = null)
      }
    }
  }

  /**
   * Periodically checks the GitHub-hosted version manifest so users do not
   * need to open the sidebar manually to discover a new APK.
   */
  private fun startAutomaticUpdateChecker() {
    viewModelScope.launch(Dispatchers.IO) {
      while (isActive) {
        delay(30 * 60 * 1000L)
        if (isActive) {
          checkForAppUpdates(isManual = false)
        }
      }
    }
  }

  fun dismissUpdateDialog() {
    val update = _uiState.value.availableUpdate
    if (update != null) {
      updatePrefs.edit().putInt("dismissed_version_code", update.versionCode).apply()
    }
    _uiState.value = _uiState.value.copy(availableUpdate = null)
  }

  fun clearUpToDateNotice() {
    _uiState.value = _uiState.value.copy(upToDateNotice = null)
  }

  /**
   * Refreshes all dates, three-day forecast (Today, Tomorrow, Day after tomorrow),
   * and live research items.
   */
  fun refreshCalendarData() {
    val now = Calendar.getInstance()
    val today = BodoSolarCalendar.convertToBodoDate(now)

    val tomCal = Calendar.getInstance().apply { add(Calendar.DATE, 1) }
    val tomorrow = BodoSolarCalendar.convertToBodoDate(tomCal)

    val dayAfterCal = Calendar.getInstance().apply { add(Calendar.DATE, 2) }
    val dayAfter = BodoSolarCalendar.convertToBodoDate(dayAfterCal)

    val curYear = _uiState.value.displayedYear
    val curMonth = _uiState.value.displayedMonth
    val days = BodoSolarCalendar.getDaysForGregorianMonth(curYear, curMonth)

    // Curated events for 3-day view
    val todayEvs = BodoHistoryDatabase.getEventsForCalendarDay(now, "Ajj (Today)")
    val tomEvs = BodoHistoryDatabase.getEventsForCalendarDay(tomCal, "Gabun (Tomorrow)")
    val dayAfterEvs = BodoHistoryDatabase.getEventsForCalendarDay(dayAfterCal, "Sampoi (Day After)")

    // Combined live messages
    val allEvents = mutableListOf<CulturalNewsItem>()
    // Add local custom admin messages first
    allEvents.addAll(supabaseManager.getLocalAdminMessages())
    allEvents.addAll(todayEvs)
    allEvents.addAll(tomEvs.take(2))

    // Filter out user-deleted items
    val activeMessages = allEvents.filter { !supabaseManager.isMessageDeleted(it.id) }

    val engMonthFmt = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(now.time)

    _uiState.value = _uiState.value.copy(
      currentSystemDate = now,
      monthDays = days,
      todayBodoDate = today,
      tomorrowBodoDate = tomorrow,
      dayAfterBodoDate = dayAfter,
      todayEvents = todayEvs.filter { !supabaseManager.isMessageDeleted(it.id) },
      tomorrowEvents = tomEvs.filter { !supabaseManager.isMessageDeleted(it.id) },
      dayAfterEvents = dayAfterEvs.filter { !supabaseManager.isMessageDeleted(it.id) },
      liveMessages = activeMessages,
      currentRunningMonthBodo = today.bodoMonth,
      currentRunningMonthEng = engMonthFmt
    )
  }

  /**
   * 12:00 AM Midnight Auto-Rollover Engine:
   * Constantly active loop that calculates exact ms until 00:00:00.
   * At midnight, date increments automatically (e.g. 2 -> 3) and refreshes live news!
   */
  private fun startMidnightAutoRolloverEngine() {
    viewModelScope.launch(Dispatchers.Default) {
      var lastRecordedDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
      while (isActive) {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_YEAR)

        if (currentDay != lastRecordedDay) {
          lastRecordedDay = currentDay
          refreshCalendarData()
          checkTodayReminders(forceAlert = false)
        }

        val millisUntilNextMinute = 60_000L - (now.get(Calendar.SECOND) * 1000L + now.get(Calendar.MILLISECOND))
        delay(millisUntilNextMinute.coerceAtLeast(1000L))
      }
    }
  }

  private fun startLiveTickerRotation() {
    viewModelScope.launch {
      while (isActive) {
        delay(6000L)
        val msgs = _uiState.value.liveMessages
        if (msgs.isNotEmpty()) {
          val next = (_uiState.value.liveTickerIndex + 1) % msgs.size
          _uiState.value = _uiState.value.copy(liveTickerIndex = next)
        }
      }
    }
  }

  private fun syncCloudAnnouncements() {
    viewModelScope.launch {
      supabaseManager.fetchCloudAnnouncements()
      refreshCalendarData()
    }
  }

  fun nextMonth() {
    var y = _uiState.value.displayedYear
    var m = _uiState.value.displayedMonth + 1
    if (m > 12) {
      m = 1
      y++
    }
    val days = BodoSolarCalendar.getDaysForGregorianMonth(y, m)
    val now = Calendar.getInstance()
    val isTodayInMonth = (now.get(Calendar.YEAR) == y && now.get(Calendar.MONTH) + 1 == m)
    val newSelected = if (isTodayInMonth) {
      BodoSolarCalendar.convertToBodoDate(now)
    } else {
      days.firstOrNull() ?: _uiState.value.selectedDate
    }
    _uiState.value = _uiState.value.copy(
      displayedYear = y,
      displayedMonth = m,
      monthDays = days,
      selectedDate = newSelected
    )
  }

  fun prevMonth() {
    var y = _uiState.value.displayedYear
    var m = _uiState.value.displayedMonth - 1
    if (m < 1) {
      m = 12
      y--
    }
    val days = BodoSolarCalendar.getDaysForGregorianMonth(y, m)
    val now = Calendar.getInstance()
    val isTodayInMonth = (now.get(Calendar.YEAR) == y && now.get(Calendar.MONTH) + 1 == m)
    val newSelected = if (isTodayInMonth) {
      BodoSolarCalendar.convertToBodoDate(now)
    } else {
      days.firstOrNull() ?: _uiState.value.selectedDate
    }
    _uiState.value = _uiState.value.copy(
      displayedYear = y,
      displayedMonth = m,
      monthDays = days,
      selectedDate = newSelected
    )
  }

  fun previousMonth() = prevMonth()

  fun goToToday() {
    val now = Calendar.getInstance()
    val y = now.get(Calendar.YEAR)
    val m = now.get(Calendar.MONTH) + 1
    val days = BodoSolarCalendar.getDaysForGregorianMonth(y, m)
    val today = BodoSolarCalendar.convertToBodoDate(now)
    _uiState.value = _uiState.value.copy(
      displayedYear = y,
      displayedMonth = m,
      monthDays = days,
      selectedDate = today
    )
  }

  fun selectDate(date: BodoDate) {
    _uiState.value = _uiState.value.copy(selectedDate = date)
  }

  fun deleteMessage(id: String) {
    supabaseManager.markMessageDeleted(id)
    val updated = _uiState.value.liveMessages.filter { it.id != id }
    _uiState.value = _uiState.value.copy(
      liveMessages = updated,
      todayEvents = _uiState.value.todayEvents.filter { it.id != id },
      tomorrowEvents = _uiState.value.tomorrowEvents.filter { it.id != id },
      dayAfterEvents = _uiState.value.dayAfterEvents.filter { it.id != id },
      liveTickerIndex = if (updated.isNotEmpty()) _uiState.value.liveTickerIndex % updated.size else 0
    )
  }

  fun publishAdminAnnouncement(
    title: String,
    bodoTitle: String,
    category: String,
    content: String,
    bodoContent: String,
    author: String
  ) {
    viewModelScope.launch {
      supabaseManager.publishAdminMessage(
        title = title,
        bodoTitle = bodoTitle,
        category = category,
        content = content,
        bodoContent = bodoContent,
        author = author
      )
      _uiState.value = _uiState.value.copy(
        adminPublishStatus = "Message published successfully & broadcasting live!"
      )
      refreshCalendarData()
      delay(2000L)
      _uiState.value = _uiState.value.copy(
        showAdminDialog = false,
        adminPublishStatus = null
      )
    }
  }

  fun toggleMonthExplanation(show: Boolean) {
    _uiState.value = _uiState.value.copy(showMonthExplanationDialog = show)
  }

  fun toggleAdminDialog(show: Boolean) {
    _uiState.value = _uiState.value.copy(showAdminDialog = show, adminPublishStatus = null)
  }

  fun toggleAppDownloadDialog(show: Boolean) {
    _uiState.value = _uiState.value.copy(showAppDownloadDialog = show)
  }
}
