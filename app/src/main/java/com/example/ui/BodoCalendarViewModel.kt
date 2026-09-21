package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BodoDate
import com.example.data.BodoHistoryDatabase
import com.example.data.BodoMonth
import com.example.data.BodoSolarCalendar
import com.example.data.CulturalNewsItem
import com.example.data.SupabaseManager
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
  val adminPublishStatus: String? = null
)

class BodoCalendarViewModel(application: Application) : AndroidViewModel(application) {

  private val supabaseManager = SupabaseManager(application)

  private val _uiState = MutableStateFlow(CalendarUiState())
  val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  init {
    refreshCalendarData()
    startMidnightAutoRolloverEngine()
    startLiveTickerRotation()
    syncCloudAnnouncements()
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
          viewModelScope.launch(Dispatchers.Main) {
            refreshCalendarData()
          }
        }

        // Calculate time to next midnight
        val nextMidnight = Calendar.getInstance().apply {
          add(Calendar.DAY_OF_YEAR, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }
        val msUntilMidnight = (nextMidnight.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)

        // Delay until midnight or periodic 10-second check
        val sleepDuration = minOf(msUntilMidnight, 10_000L)
        delay(sleepDuration)
      }
    }
  }

  /**
   * Rotates live ticker message every 6 seconds
   */
  private fun startLiveTickerRotation() {
    viewModelScope.launch(Dispatchers.Default) {
      while (isActive) {
        delay(6000L)
        val size = _uiState.value.liveMessages.size
        if (size > 1) {
          _uiState.value = _uiState.value.copy(
            liveTickerIndex = (_uiState.value.liveTickerIndex + 1) % size
          )
        }
      }
    }
  }

  private fun syncCloudAnnouncements() {
    viewModelScope.launch {
      val remote = supabaseManager.fetchSupabaseAnnouncements()
      if (remote.isNotEmpty()) {
        val current = _uiState.value.liveMessages.toMutableList()
        remote.forEach { item ->
          if (current.none { it.id == item.id } && !supabaseManager.isMessageDeleted(item.id)) {
            current.add(0, item)
          }
        }
        _uiState.value = _uiState.value.copy(liveMessages = current)
      }
    }
  }

  /**
   * Month Navigation
   */
  fun previousMonth() {
    var y = _uiState.value.displayedYear
    var m = _uiState.value.displayedMonth - 1
    if (m < 1) {
      m = 12
      y -= 1
    }
    _uiState.value = _uiState.value.copy(
      displayedYear = y,
      displayedMonth = m,
      monthDays = BodoSolarCalendar.getDaysForGregorianMonth(y, m)
    )
  }

  fun nextMonth() {
    var y = _uiState.value.displayedYear
    var m = _uiState.value.displayedMonth + 1
    if (m > 12) {
      m = 1
      y += 1
    }
    _uiState.value = _uiState.value.copy(
      displayedYear = y,
      displayedMonth = m,
      monthDays = BodoSolarCalendar.getDaysForGregorianMonth(y, m)
    )
  }

  fun goToToday() {
    val now = Calendar.getInstance()
    val y = now.get(Calendar.YEAR)
    val m = now.get(Calendar.MONTH) + 1
    _uiState.value = _uiState.value.copy(
      displayedYear = y,
      displayedMonth = m,
      selectedDate = BodoSolarCalendar.convertToBodoDate(now),
      monthDays = BodoSolarCalendar.getDaysForGregorianMonth(y, m)
    )
  }

  fun selectDate(date: BodoDate) {
    _uiState.value = _uiState.value.copy(selectedDate = date)
  }

  /**
   * User can delete/dismiss any live cultural message or announcement
   */
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

  /**
   * Publish custom announcement from Admin Panel
   */
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
