package com.example

import com.example.data.BodoHistoryDatabase
import com.example.data.BodoMonth
import com.example.data.BodoSolarCalendar
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
  @Test
  fun testBodoSolarConversionForSeptember() {
    val cal = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 20)
    }
    val bodoDate = BodoSolarCalendar.convertToBodoDate(cal)
    assertEquals(BodoMonth.AHIN, bodoDate.bodoMonth)
    assertTrue("Bodo day should be positive", bodoDate.bodoDay > 0)
    assertEquals(20, bodoDate.gregorianDay)
    assertEquals(9, bodoDate.gregorianMonth)
    assertEquals(2026, bodoDate.gregorianYear)
  }

  @Test
  fun testHistoricalEventsGeneration() {
    val cal = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 20)
    }
    val events = BodoHistoryDatabase.getEventsForCalendarDay(cal, "Today")
    assertTrue("Historical research events should not be empty", events.isNotEmpty())
  }
}
