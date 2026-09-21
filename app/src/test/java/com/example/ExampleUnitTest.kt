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
    val cal21 = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 21)
    }
    val bodoDate21 = BodoSolarCalendar.convertToBodoDate(cal21)
    assertEquals(BodoMonth.AHIN, bodoDate21.bodoMonth)
    assertEquals("September 21, 2026 should be Bodo Day 3", 3, bodoDate21.bodoDay)
    assertEquals(21, bodoDate21.gregorianDay)
    assertEquals(9, bodoDate21.gregorianMonth)
    assertEquals(2026, bodoDate21.gregorianYear)

    val cal20 = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 20)
    }
    val bodoDate20 = BodoSolarCalendar.convertToBodoDate(cal20)
    assertEquals(2, bodoDate20.bodoDay)
    assertEquals(BodoMonth.AHIN, bodoDate20.bodoMonth)

    val cal19 = Calendar.getInstance().apply {
      set(2026, Calendar.SEPTEMBER, 19)
    }
    val bodoDate19 = BodoSolarCalendar.convertToBodoDate(cal19)
    assertEquals(1, bodoDate19.bodoDay)
    assertEquals(BodoMonth.AHIN, bodoDate19.bodoMonth)
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
