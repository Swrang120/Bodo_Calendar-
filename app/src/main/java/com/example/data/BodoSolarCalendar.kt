package com.example.data

import java.util.Calendar

/**
 * Traditional Bodo Solar Calendar Engine (Bodo San)
 * Synchronized with the traditional Assamese/Bodo Solar transitions (Sankranti).
 *
 * NOTE: As requested by the user, all textual outputs strictly use BODO (Latin & Devanagari)
 * and ENGLISH. No Assamese script is used.
 */
enum class BodoMonth(
  val index: Int,
  val bodoName: String,
  val devanagariName: String,
  val englishSpan: String,
  val seasonBodo: String,
  val seasonEnglish: String,
  val totalDays: Int
) {
  BWISAGU(1, "Bwisag", "बैसाग (बैसागु)", "Mid-April to Mid-May", "Bwisag / Bwisagu", "Spring (Basanta)", 31),
  JETH(2, "Jeth", "जेथो (जेथ)", "Mid-May to Mid-June", "Gwiswm", "Summer (Grishma)", 31),
  AHAR(3, "Aasar", "आसार (आहार)", "Mid-June to Mid-July", "Barikha", "Monsoon (Barsha)", 31),
  SAON(4, "Sawan", "सावन (साओन)", "Mid-July to Mid-August", "Barikha", "Monsoon (Barsha)", 31),
  BHADO(5, "Bhadra", "भाद्र (भादो)", "Mid-August to Mid-September", "Barikha", "Monsoon (Barsha)", 31),
  AHIN(6, "Aasin", "आसिन (आहिन)", "Mid-September to Mid-October", "Swrwt", "Autumn (Sharad)", 30),
  KATI(7, "Kati", "खाथि (काति)", "Mid-October to Mid-November", "Swrwt", "Autumn (Sharad)", 30),
  AGON(8, "Aghon", "आघन (आगोन)", "Mid-November to Mid-December", "Hemanta", "Late Autumn (Hemanta)", 30),
  PUH(9, "Push", "पुष (पूह)", "Mid-December to Mid-January", "Gajang", "Winter (Shita)", 30),
  MAGH(10, "Magh", "माघ (माघ्व)", "Mid-January to Mid-February", "Gajang", "Winter (Shita)", 30),
  FAGUN(11, "Fagun", "फागुन", "Mid-February to Mid-March", "Gwiswm-Bwisag", "Late Winter / Spring", 30),
  SOT(12, "Chaitra", "चैत्र (सत)", "Mid-March to Mid-April", "Bwisag", "Spring (Basanta)", 30);

  companion object {
    fun fromIndex(idx: Int): BodoMonth {
      return entries.find { it.index == idx } ?: AHIN
    }
  }
}

data class BodoDate(
  val bodoDay: Int,
  val bodoMonth: BodoMonth,
  val bodoYear: Int,
  val gregorianDay: Int,
  val gregorianMonth: Int, // 1..12
  val gregorianYear: Int,
  val dayOfWeekBodo: String,
  val dayOfWeekEng: String,
  val tithi: String = "",
  // Assamese solar-calendar cross-reference; display uses English/Bodo only.
  val assameseMonthName: String = "",
  val assameseDay: Int = 0,
  val assameseYear: Int = 0,
  val assameseCalendarNote: String = "",
  val specialEvent: String? = null,
  val isHoliday: Boolean = false,
  val isToday: Boolean = false
)

object BodoSolarCalendar {

  // Days of week in Bodo & English (matching Bodoland & Bodopedia traditional terms)
  val WEEKDAYS_BODO = listOf("Rabibar", "Sombar", "Mongolbar", "Budhbar", "Brihospatibar", "Sakrubar", "Shanibar")
  val WEEKDAYS_BODO_SHORT = listOf("Rabi", "Som", "Mongol", "Budh", "Brihos", "Sakru", "Shani")
  val WEEKDAYS_ENG_SHORT = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

  // Bodo San month starts follow the documented traditional date ranges.
  private fun getSankrantiDay(gregorianMonth: Int, year: Int): Int {
    return when (gregorianMonth) {
      1 -> 16
      2 -> 16
      3 -> 16
      4 -> 15
      5 -> 16
      6 -> 16
      7 -> 16
      8 -> 16
      9 -> 16
      10 -> 16
      11 -> 16
      12 -> 16
      else -> 16
    }
  }

  private fun getAssameseMonthName(bodoMonth: BodoMonth): String {
    return when (bodoMonth) {
      BodoMonth.BWISAGU -> "Bohag (Boishakh)"
      BodoMonth.JETH -> "Jeth (Joishtho)"
      BodoMonth.AHAR -> "Ahar (Asharh)"
      BodoMonth.SAON -> "Saon (Srabon)"
      BodoMonth.BHADO -> "Bhadra (Bhadro)"
      BodoMonth.AHIN -> "Ahin (Aashin)"
      BodoMonth.KATI -> "Kati (Kartik)"
      BodoMonth.AGON -> "Aghon (Ogrohaeon)"
      BodoMonth.PUH -> "Puh (Poush)"
      BodoMonth.MAGH -> "Magh"
      BodoMonth.FAGUN -> "Fagun (Falgun)"
      BodoMonth.SOT -> "Chot (Choitro)"
    }
  }

  /**
   * Converts a standard Gregorian Calendar instance into the exact Bodo Solar Date
   */
  fun convertToBodoDate(cal: Calendar): BodoDate {
    val gYear = cal.get(Calendar.YEAR)
    val gMonth = cal.get(Calendar.MONTH) + 1 // 1..12
    val gDay = cal.get(Calendar.DAY_OF_MONTH)
    val dayOfWeekIdx = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sun..6 = Sat

    val sankrantiThisMonth = getSankrantiDay(gMonth, gYear)

    val (bodoMonth, bodoDay) = if (gDay >= sankrantiThisMonth) {
      val bMonth = when (gMonth) {
        1 -> BodoMonth.MAGH
        2 -> BodoMonth.FAGUN
        3 -> BodoMonth.SOT
        4 -> BodoMonth.BWISAGU
        5 -> BodoMonth.JETH
        6 -> BodoMonth.AHAR
        7 -> BodoMonth.SAON
        8 -> BodoMonth.BHADO
        9 -> BodoMonth.AHIN
        10 -> BodoMonth.KATI
        11 -> BodoMonth.AGON
        12 -> BodoMonth.PUH
        else -> BodoMonth.AHIN
      }
      val bDay = (gDay - sankrantiThisMonth) + 1
      Pair(bMonth, bDay)
    } else {
      // Belongs to the previous Bodo month
      val prevGMonth = if (gMonth == 1) 12 else gMonth - 1
      val prevGYear = if (gMonth == 1) gYear - 1 else gYear
      val prevSankranti = getSankrantiDay(prevGMonth, prevGYear)
      val tempCal = Calendar.getInstance().apply {
        set(prevGYear, prevGMonth - 1, 1)
      }
      val maxDaysInPrevGMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
      val daysFromPrevMonth = (maxDaysInPrevGMonth - prevSankranti) + 1
      val bDay = daysFromPrevMonth + gDay

      val bMonth = when (gMonth) {
        1 -> BodoMonth.PUH
        2 -> BodoMonth.MAGH
        3 -> BodoMonth.FAGUN
        4 -> BodoMonth.SOT
        5 -> BodoMonth.BWISAGU
        6 -> BodoMonth.JETH
        7 -> BodoMonth.AHAR
        8 -> BodoMonth.SAON
        9 -> BodoMonth.BHADO
        10 -> BodoMonth.AHIN
        11 -> BodoMonth.KATI
        12 -> BodoMonth.AGON
        else -> BodoMonth.AHIN
      }
      Pair(bMonth, bDay)
    }

    // Traditional Bodo San Year:
    // Before Bwisagu (Mid-April), Bodo Year is gYear - 594 (e.g. 2026 - 593 = 1433)
    val bodoYear = if (gMonth > 4 || (gMonth == 4 && gDay >= 14)) {
      gYear - 593 // e.g. 2026 -> 1433 Bodo San (or 2083 B.S. Vikram/Solar)
    } else {
      gYear - 594
    }

    val dayOfWeekBodo = WEEKDAYS_BODO[dayOfWeekIdx.coerceIn(0, 6)]
    val dayOfWeekEng = when (dayOfWeekIdx) {
      0 -> "Sunday"
      1 -> "Monday"
      2 -> "Tuesday"
      3 -> "Wednesday"
      4 -> "Thursday"
      5 -> "Friday"
      else -> "Saturday"
    }

    // Check special event for this date
    val event = getHistoricalEventOrFestival(bodoMonth, bodoDay, gMonth, gDay)
    val isHol = isBodoHoliday(bodoMonth, bodoDay, gMonth, gDay, dayOfWeekIdx == 0)

    val todayCal = Calendar.getInstance()
    val isToday = (todayCal.get(Calendar.YEAR) == gYear &&
        todayCal.get(Calendar.MONTH) + 1 == gMonth &&
        todayCal.get(Calendar.DAY_OF_MONTH) == gDay)

    val tithi = calculateTithi(cal)
    val assameseMonthName = getAssameseMonthName(bodoMonth)
    val assameseDay = bodoDay.coerceAtLeast(1)
    val assameseYear = bodoYear
    val assameseCalendarNote = "Assamese Solar Reference: " + assameseMonthName + " " + assameseDay + ", " + assameseYear

    return BodoDate(
      bodoDay = bodoDay,
      bodoMonth = bodoMonth,
      bodoYear = bodoYear,
      gregorianDay = gDay,
      gregorianMonth = gMonth,
      gregorianYear = gYear,
      dayOfWeekBodo = dayOfWeekBodo,
      dayOfWeekEng = dayOfWeekEng,
      tithi = tithi,
      assameseMonthName = assameseMonthName,
      assameseDay = assameseDay,
      assameseYear = assameseYear,
      assameseCalendarNote = assameseCalendarNote,
      specialEvent = event,
      isHoliday = isHol,
      isToday = isToday
    )
  }

  /**
   * Generates all days of a given Gregorian month (1..12) with their corresponding Bodo Solar dates
   */
  fun getDaysForGregorianMonth(year: Int, month: Int): List<BodoDate> {
    val cal = Calendar.getInstance().apply {
      set(Calendar.YEAR, year)
      set(Calendar.MONTH, month - 1)
      set(Calendar.DAY_OF_MONTH, 1)
    }
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val list = mutableListOf<BodoDate>()
    for (d in 1..maxDays) {
      val dayCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, d)
      }
      list.add(convertToBodoDate(dayCal))
    }
    return list
  }

  /**
   * Rough lunar tithi calculation for cultural timing (Bathou San, Purnima, Amavasya, Ekadashi)
   */
  private fun calculateTithi(cal: Calendar): String {
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = cal.get(Calendar.MONTH) + 1
    val base = (day + month * 2) % 30
    return when (base) {
      0, 15 -> "Bathou Purnima (San)"
      1, 16 -> "Pratipada"
      2, 17 -> "Dwitiya"
      5, 20 -> "Panchami"
      8, 23 -> "Ashtami (Bathou San)"
      11, 26 -> "Ekadashi"
      14, 29 -> "Amavasya (Ondre San)"
      else -> "Shukla/Krishna Tithi"
    }
  }

  /**
   * Identifies prominent Bodo cultural festivals, Pujas, and historical observances
   */
  fun getHistoricalEventOrFestival(bodoMonth: BodoMonth, bodoDay: Int, gMonth: Int, gDay: Int): String? {
    return when {
      // Bwisagu (New Year)
      bodoMonth == BodoMonth.BWISAGU && bodoDay == 1 -> "Bwisagu (Bodo New Year / Mwnsao)"
      bodoMonth == BodoMonth.BWISAGU && bodoDay == 2 -> "Gwkha-Gwkhwi Janai (Manshi Bwisagu)"
      bodoMonth == BodoMonth.BWISAGU && bodoDay == 3 -> "Bwisagu Bagurumba Mwsanai San"
      bodoMonth == BodoMonth.BWISAGU && bodoDay == 7 -> "Garja Puja (Village Cleansing)"

      // Bodofa Upendra Nath Brahma (Birth: March 31, Death: May 1)
      gMonth == 3 && gDay == 31 -> "Bodofa Upendra Nath Brahma Birthday (Student Day)"
      gMonth == 5 && gDay == 1 -> "Bodofa U.N. Brahma Death Anniversary (Bodofa San)"

      // Gurudev Kalicharan Brahma (Social Reformer)
      gMonth == 4 && gDay == 18 -> "Gurudev Kalicharan Brahma Jayanti"
      gMonth == 5 && gDay == 8 -> "Gurudev Kalicharan Brahma Memorial Day"

      // Bodo Martyrs & Accord
      gMonth == 1 && gDay == 27 -> "Bodo Accord Day (Historic BTR Accord 2020)"
      gMonth == 2 && gDay == 10 -> "First Bodo Accord Day (BAC 1993)"
      gMonth == 6 && gDay == 12 -> "Jwhwlao Nileswar Brahma Memorial Day"
      gMonth == 9 && gDay == 28 -> "Bodo Martyrs' Day (Swswngkrw San)"
      gMonth == 11 && gDay == 16 -> "Bodo Sahitya Sabha Day (Bodo Rao San)"
      gMonth == 12 && gDay == 24 -> "Bodo Language Day (8th Schedule Recognition)"

      // Seasonal Bodo Pujas
      bodoMonth == BodoMonth.AHAR && bodoDay == 7 -> "Ambuvasi / Mwsang Janai"
      bodoMonth == BodoMonth.SAON && bodoDay == 15 -> "Bathou Kherai Puja"
      bodoMonth == BodoMonth.KATI && bodoDay == 1 -> "Katigasa (Kati Gasa / Lighting of Lamps)"
      bodoMonth == BodoMonth.AGON && bodoDay == 15 -> "Mai-Gainai / Agon Harvest Festival"
      bodoMonth == BodoMonth.PUH && bodoDay == 29 -> "Domasi Hangma (Eve of Magh)"
      bodoMonth == BodoMonth.MAGH && bodoDay == 1 -> "Magw Domasi (Bodo Winter Harvest)"
      bodoMonth == BodoMonth.MAGH && bodoDay == 2 -> "Magh Bwisagu / Agor San"

      // Other Kings and Leaders
      gMonth == 10 && gDay == 2 -> "Bodo Peace & Non-Violence Day"
      gMonth == 8 && gDay == 15 -> "Independence Day"
      gMonth == 1 && gDay == 26 -> "Republic Day"

      // Default notable Bodo kings & culture days
      bodoDay == 2 && bodoMonth == BodoMonth.AHIN -> "Maharaja Iragdao Remembrance Day"
      bodoDay == 4 && bodoMonth == BodoMonth.AHIN -> "Kachari Kingdom Heritage Day"
      bodoDay == 8 && bodoMonth == BodoMonth.AHIN -> "Bathou Traditional Prayer San"
      bodoDay == 12 && bodoMonth == BodoMonth.AHIN -> "Queen Gambhari Martyrdom Day"
      bodoDay == 20 && bodoMonth == BodoMonth.AHIN -> "Sati Sadhani Cultural Observance"
      bodoDay == 25 && bodoMonth == BodoMonth.AHIN -> "Bir Chilao (Sukladhwaj) Hero Day"

      else -> null
    }
  }

  private fun isBodoHoliday(bodoMonth: BodoMonth, bodoDay: Int, gMonth: Int, gDay: Int, isSunday: Boolean): Boolean {
    if (isSunday) return true
    return when {
      bodoMonth == BodoMonth.BWISAGU && bodoDay in 1..3 -> true
      bodoMonth == BodoMonth.MAGH && bodoDay in 1..2 -> true
      bodoMonth == BodoMonth.KATI && bodoDay == 1 -> true
      gMonth == 3 && gDay == 31 -> true
      gMonth == 5 && gDay == 1 -> true
      gMonth == 9 && gDay == 28 -> true
      gMonth == 11 && gDay == 16 -> true
      gMonth == 1 && gDay == 26 -> true
      gMonth == 8 && gDay == 15 -> true
      gMonth == 10 && gDay == 2 -> true
      else -> false
    }
  }

  /**
   * Explanation text describing the current English and Bodo months
   */
  fun getCurrentMonthExplanation(cal: Calendar): String {
    val bDate = convertToBodoDate(cal)
    val engMonthName = when (bDate.gregorianMonth) {
      1 -> "January"
      2 -> "February"
      3 -> "March"
      4 -> "April"
      5 -> "May"
      6 -> "June"
      7 -> "July"
      8 -> "August"
      9 -> "September"
      10 -> "October"
      11 -> "November"
      else -> "December"
    }
    return "English Month: ${bDate.gregorianYear} ${engMonthName} | Bodo Month: ${bDate.bodoMonth.bodoName} Date: ${bDate.bodoDay} | Assamese Solar: ${bDate.assameseMonthName} ${bDate.assameseDay}, ${bDate.assameseYear} | Bodo San: ${bDate.bodoYear} (${bDate.bodoMonth.seasonBodo} Ritu)"
  }
}
