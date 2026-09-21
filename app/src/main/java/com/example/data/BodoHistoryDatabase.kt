package com.example.data

import java.util.Calendar

data class CulturalNewsItem(
  val id: String,
  val title: String,
  val bodoTitle: String,
  val category: String, // "Raja/Monarch", "Leader", "Puja", "History", "News", "Admin"
  val description: String,
  val bodoDescription: String,
  val dateLabel: String, // "Ajj (Today)", "Gabun (Tomorrow)", "Sampoi (Day After)"
  val fullDate: String,
  val author: String = "Bodo Culture System",
  val timestamp: Long = System.currentTimeMillis(),
  val isCustomAdmin: Boolean = false
)

object BodoHistoryDatabase {

  /**
   * Generates live cultural news and historical research for a given calendar date
   */
  fun getEventsForCalendarDay(cal: Calendar, dayOffsetLabel: String): List<CulturalNewsItem> {
    val bDate = BodoSolarCalendar.convertToBodoDate(cal)
    val gMonth = bDate.gregorianMonth
    val gDay = bDate.gregorianDay
    val bMonth = bDate.bodoMonth
    val bDay = bDate.bodoDay

    val list = mutableListOf<CulturalNewsItem>()

    // Day-specific historical events based on Gregorian and Bodo Solar dates
    val items = getCuratedHistoricalEvents(bMonth, bDay, gMonth, gDay, dayOffsetLabel)
    list.addAll(items)

    // Ensure every single day has at least 2 rich cultural research items
    if (list.size < 2) {
      list.add(getDailyBodoCulturalHeritage(bMonth, bDay, gMonth, gDay, dayOffsetLabel))
      list.add(getBathouAndPujaObservance(bDate.dayOfWeekBodo, dayOffsetLabel))
    }

    return list
  }

  private fun getCuratedHistoricalEvents(
    bMonth: BodoMonth,
    bDay: Int,
    gMonth: Int,
    gDay: Int,
    dayLabel: String
  ): List<CulturalNewsItem> {
    val list = mutableListOf<CulturalNewsItem>()

    // Specific famous dates:
    when {
      // March 31: Bodofa U.N. Brahma Birth
      gMonth == 3 && gDay == 31 -> {
        list.add(
          CulturalNewsItem(
            id = "bodofa_birth_$gMonth$gDay",
            title = "Bodofa Upendra Nath Brahma Birthday (Student Day)",
            bodoTitle = "Bodofa U.N. Brahma-ni Jonmo San (Fwrwnggiri San)",
            category = "Leader",
            description = "Today marks the birth anniversary of Bodofa Upendra Nath Brahma (1956-1990), visionary leader and Father of the Bodo nation who championed democratic struggle with the motto 'Live and Let Live'.",
            bodoDescription = "Dinwihw Bodo firfani apha Bodofa Upendra Nath Brahma-ni jonmo san. Bithanga Bodo rao, thunlai aro jati-ni thakhai jiu bawlangbwbai.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / $gDay March"
          )
        )
      }

      // May 1: Bodofa U.N. Brahma Death
      gMonth == 5 && gDay == 1 -> {
        list.add(
          CulturalNewsItem(
            id = "bodofa_death_$gMonth$gDay",
            title = "Bodofa Upendra Nath Brahma Memorial Day (Death Anniversary)",
            bodoTitle = "Bodofa U.N. Brahma-ni Thwinawi Gaswkhang San",
            category = "Leader",
            description = "Commemoration of the martyrdom and passing of Bodofa Upendra Nath Brahma in 1990. Observed across Bodoland with floral tributes and cultural pledges.",
            bodoDescription = "Bodofa Upendra Nath Brahma-khwo gwsw khangnanwi Bodo hafanai gubun gubun jayagao bibar bawwi san-khwo palinaibwbai.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / $gDay May"
          )
        )
      }

      // April 18: Gurudev Kalicharan Brahma Birth
      gMonth == 4 && gDay == 18 -> {
        list.add(
          CulturalNewsItem(
            id = "kalicharan_birth_$gMonth$gDay",
            title = "Gurudev Kalicharan Brahma Birth Anniversary",
            bodoTitle = "Gurudev Kalicharan Brahma-ni Jonmo San",
            category = "Leader",
            description = "Gurudev Kalicharan Brahma (1860-1938) was the revolutionary social reformer who introduced Brahma Dharma, modern education, and socio-economic upliftment among the Bodos.",
            bodoDescription = "Gurudev Kalicharan Brahma-ni jonmo san. Bithangnw Bodo rajaphwo-khwo gidingnwi lekhapora aro samaj sungdung hwdwngmwn.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / $gDay April"
          )
        )
      }

      // September 20 - 22: Ahin Month specials & Historic Monarchs
      (gMonth == 9 && gDay in 18..23) || (bMonth == BodoMonth.AHIN && bDay in 1..6) -> {
        list.add(
          CulturalNewsItem(
            id = "raja_iragdao_$gMonth$gDay",
            title = "Maharaja Iragdao & Kachari Kingdom Historical Glory",
            bodoTitle = "Raja Iragdao aro Bodo Kachari Ha-der-ni Jarimin",
            category = "Raja/Monarch",
            description = "Historical research: Remembering King Iragdao and the glorious monarchs of the ancient Kachari Kingdom (Dimapur, Maibang, Khaspur). Their bravery preserved Bodo indigenous heritage, traditional Bathou faith, and silk weaving craftsmanship.",
            bodoDescription = "Bodo raja Iragdao aro Kachari rajaphwrni jarimin: Bithangsura Bodo rao, Bathou dhwrmw aro Aronai agor agan-khwo rakhibwbai.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay (Bodo San) / $gDay September"
          )
        )
        list.add(
          CulturalNewsItem(
            id = "queen_gambhari_$gMonth$gDay",
            title = "Queen Gambhari & Legendary Bodo Warriors Remembrance",
            bodoTitle = "Rani Gambhari aro Bodo Jwhwlaophwrni Jarimin",
            category = "History",
            description = "Honoring the valiance of Bodo Queen Gambhari and legendary warriors (Jwhwlao) who fought fearlessly to defend the motherland and its cultural sovereignty in ancient times.",
            bodoDescription = "Bodo rani Gambhari aro Jwhwlaophwrni udangsri daoha-ni jarimin khwo dinwi man honw hwbwbai.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / $gDay September"
          )
        )
      }

      // September 28: Bodo Martyrs Day
      gMonth == 9 && gDay == 28 -> {
        list.add(
          CulturalNewsItem(
            id = "martyrs_day_$gMonth$gDay",
            title = "Bodo Martyrs' Day (Swswngkrw San)",
            bodoTitle = "Bodo Swswngkrw San (Martyrs' Day)",
            category = "Martyr",
            description = "Sacred tribute to thousands of brave Bodo sons and daughters who made the supreme sacrifice during the movement for political identity and self-determination.",
            bodoDescription = "Bodo jati-ni thakhai jiu bawlangnay hajir hajir swswngkrwphwrnw gwbwr bawnaibwbai.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / 28 September"
          )
        )
      }

      // November 16: Bodo Sahitya Sabha Day
      gMonth == 11 && gDay == 16 -> {
        list.add(
          CulturalNewsItem(
            id = "bss_day_$gMonth$gDay",
            title = "Bodo Sahitya Sabha Foundation Day (Bodo Rao San)",
            bodoTitle = "Bodo Sahitya Sabha-ni Gaikhenai San",
            category = "Literature",
            description = "Founded in 1952 at Basugaon, Bodo Sahitya Sabha (BSS) is the apex literary body that standardized the Bodo language and secured its recognition in the 8th Schedule of the Indian Constitution.",
            bodoDescription = "1952 mythaio Basugaon-ao Bodo Sahitya Sabha gaikhedwngmwn. Bodo rao aro thunlai-ni thakhai eshe gwnang san.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / 16 November"
          )
        )
      }

      // December 24: Bodo Language Day
      gMonth == 12 && gDay == 24 -> {
        list.add(
          CulturalNewsItem(
            id = "language_day_$gMonth$gDay",
            title = "Bodo Language Day (8th Schedule Inclusion 2003)",
            bodoTitle = "Bodo Rao San (8th Schedule-ao Hakhomnay)",
            category = "History",
            description = "On December 24, 2003, Bodo language received historic recognition by being incorporated into the 8th Schedule of the Indian Constitution, fulfilling a lifelong dream.",
            bodoDescription = "2003 mythaio Bodo rao-a Bharot Samvidhan-ni 8th Schedule-ao hakhomdwngmwn.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / 24 December"
          )
        )
      }

      // January 27: Historic Bodo Accord Day
      gMonth == 1 && gDay == 27 -> {
        list.add(
          CulturalNewsItem(
            id = "btr_accord_$gMonth$gDay",
            title = "Historic BTR Accord Day (Bodoland Peace Accord)",
            bodoTitle = "BTR Gorobtha San (Bodoland Peace Accord)",
            category = "History",
            description = "Signed on January 27, 2020 in New Delhi between Central Government, Assam Government, and Bodo leadership, ushering an era of permanent peace, progress, and harmony.",
            bodoDescription = "2020 mythaio 27 January-ao BTR gorobtha jaidwngmwn, jahaonw Bodo hafangao santi aro phwnangnay phwidwng.",
            dateLabel = dayLabel,
            fullDate = "${bMonth.bodoName} $bDay / 27 January"
          )
        )
      }

      // Bwisagu (Bodo New Year) Month 1 Day 1
      bMonth == BodoMonth.BWISAGU && bDay == 1 -> {
        list.add(
          CulturalNewsItem(
            id = "bwisagu_day1_$gMonth$gDay",
            title = "Bwisagu Festival Day 1: Maku/Makhau (Cattle Worship)",
            bodoTitle = "Bwisagu Sehti San: Mwsao-khwo Dwi Lugunai San",
            category = "Festival",
            description = "Grand start of Bwisagu! Traditional ritual of bathing domestic cattle at riverside using wild turmeric (Haldi), gourd (Lau), and black brinjal, singing folk blessings for abundance.",
            bodoDescription = "Bwisagu-ni sehti san. Mwsao-khwo haldi, lau, phanthao jonanwi dwi luguyw aro aashirbad laiyw.",
            dateLabel = dayLabel,
            fullDate = "Bwisagu 1 (Bodo New Year)"
          )
        )
      }

      // Bwisagu Day 2: Human Day & Bagurumba
      bMonth == BodoMonth.BWISAGU && bDay == 2 -> {
        list.add(
          CulturalNewsItem(
            id = "bwisagu_bagurumba_$gMonth$gDay",
            title = "Manshi Bwisagu & Bagurumba Dance Celebrations",
            bodoTitle = "Manshi Bwisagu aro Bagurumba Mwsanai San",
            category = "Festival",
            description = "People wear traditional yellow Dokhona and golden Aronai, seeking blessings from elders and dancing the world-famous Bagurumba folk dance to the beats of Kham, Siphung, and Serja.",
            bodoDescription = "Gwdan Dokhona aro Aronai gananwi deraphwrnw khulumnanwi aashirbad laiyw aro Bagurumba mwsayw.",
            dateLabel = dayLabel,
            fullDate = "Bwisagu 2"
          )
        )
      }
    }

    return list
  }

  private fun getDailyBodoCulturalHeritage(
    bMonth: BodoMonth,
    bDay: Int,
    gMonth: Int,
    gDay: Int,
    dayLabel: String
  ): CulturalNewsItem {
    val dailyHighlights = listOf(
      Pair(
        "Ancient Kings of Pragjyotisha & Bodo Dynasties",
        "Historical chronicles indicate that Bodo-Kachari rulers governed vast tracts of the Brahmaputra valley. Monarchs maintained sophisticated water irrigation (Dong systems) and tribal democratic village councils (Gaonbura)."
      ),
      Pair(
        "Aronai Cultural Scarf & Sacred Agor Weaving",
        "The Aronai is the highest symbol of honor in Bodo culture. Handwoven by skilled Bodo weavers on the 'Daosri' loom, featuring motifs like Hajw Agor (mountain), Paro Megon (pigeon eye), and Daorai Kherai (peacock feather)."
      ),
      Pair(
        "Kherai Puja: The Ancient Spiritual Dance Ritual",
        "The sacred Bathou ritual of Kherai involves the Doudini (priestess) dancing with sword and shield (Thungri and Dahal) before the holy Siju tree, invoking 18 divine deities for peace and protection."
      ),
      Pair(
        "The Traditional Bodo Musical Instruments",
        "The soul of Bodo music resides in the Siphung (long five-hole bamboo flute), Kham (deep two-headed drum), Serja (four-stringed bowed violin), Jotha (brass cymbals), and Thorkha (bamboo clapper)."
      ),
      Pair(
        "King Makardhwaj & The Golden Era of Dimapur",
        "The stone monoliths and majestic gateways of Dimapur still stand testimony to the monumental architectural and engineering brilliance of the Kachari Bodo kings in the 13th to 16th centuries."
      ),
      Pair(
        "Dokhona: The Eternal Elegance of Bodo Women",
        "Woven traditionally from golden Muga and Eri silk with vibrant yellow, orange, and crimson borders. Dokhona symbolizes the dignity, grace, and industrious spirit of Bodo womanhood."
      )
    )

    val selected = dailyHighlights[(bDay + gDay + gMonth) % dailyHighlights.size]

    return CulturalNewsItem(
      id = "daily_heritage_${bMonth.index}_$bDay",
      title = selected.first,
      bodoTitle = "Bodo Harimu Jarimin: ${selected.first}",
      category = "History",
      description = selected.second,
      bodoDescription = "Bodo jati-ni jarimin aro harimu-ni bikhayao dinwi gwnang khobor. Bodo daorai, kherai aro aronai agor harimu-ni man.",
      dateLabel = dayLabel,
      fullDate = "${bMonth.bodoName} $bDay (Bodo San) / $gDay-$gMonth"
    )
  }

  private fun getBathouAndPujaObservance(dayOfWeekBodo: String, dayLabel: String): CulturalNewsItem {
    return CulturalNewsItem(
      id = "bathou_worship_$dayOfWeekBodo",
      title = "Bathou Traditional Prayer & Siju Tree San ($dayOfWeekBodo)",
      bodoTitle = "Bathou Dhwrmw aro Siju Phafani Khulumnaikho San",
      category = "Puja",
      description = "Today's spiritual reflection: Bathouism venerates 'Aboriginal Supreme Soul' (Sibrai / Obonglaoree). Families offer holy water (Dwi-Gwsar) and light mustard oil lamps before the holy 5-ridged Siju plant (Euphorbia splendens).",
      bodoDescription = "Sibrai / Obonglaoree-khwo Bathou Siju fafani sigangao dwi-gwsar phwrananwi bathi sungnanwi khulumnai janiu.",
      dateLabel = dayLabel,
      fullDate = "$dayOfWeekBodo / Daily Spiritual Guidance"
    )
  }
}
