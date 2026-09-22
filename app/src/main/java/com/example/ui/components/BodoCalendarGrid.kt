package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BodoDate
import com.example.data.BodoSolarCalendar
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldDark
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiGreen
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiRed
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiSurfaceVariant
import com.example.ui.theme.AronaiTextSecondary
import com.example.ui.theme.AronaiWarmWhite
import java.util.Calendar

@Composable
fun BodoCalendarGrid(
  displayedYear: Int,
  displayedMonth: Int,
  monthDays: List<BodoDate>,
  selectedDate: BodoDate,
  onDateSelected: (BodoDate) -> Unit,
  onPrevMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onGoToToday: () -> Unit,
  onOpenMonthInfo: () -> Unit,
  datesWithNotes: Set<String> = emptySet(),
  onOpenNotesForDate: (BodoDate) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val engMonthName = when (displayedMonth) {
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

  // Find predominant Bodo months for this display
  val firstBodoMonth = monthDays.firstOrNull()?.bodoMonth?.bodoName ?: "Ahin"
  val lastBodoMonth = monthDays.lastOrNull()?.bodoMonth?.bodoName ?: "Kati"

  // Active Bodo Date for Header:
  // If selectedDate is inside the currently viewed month, show selectedDate.
  // Otherwise, use the 15th of the viewed month (or first day) so the header ALWAYS matches the displayed month!
  val activeHeaderBodoDate = if (selectedDate.gregorianMonth == displayedMonth && selectedDate.gregorianYear == displayedYear) {
    selectedDate
  } else {
    monthDays.getOrNull(14) ?: monthDays.firstOrNull() ?: selectedDate
  }

  var dragDistanceX by remember { mutableStateOf(0f) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(AronaiSurface)
      .border(1.dp, AronaiSurfaceVariant, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    // Month Navigator Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onPrevMonth,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(AronaiNavy)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
          contentDescription = "Previous Month",
          tint = AronaiGold
        )
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onOpenMonthInfo() }
      ) {
        // Prominent Bodo Month (Always synchronized with active displayed month and selected date)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${activeHeaderBodoDate.bodoMonth.bodoName} (${activeHeaderBodoDate.bodoMonth.devanagariName})",
            color = AronaiGold,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "(${activeHeaderBodoDate.bodoYear} B.S.)",
            color = AronaiGoldLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
        // English Month reference
        Text(
          text = "$engMonthName $displayedYear (English)",
          color = AronaiWarmWhite.copy(alpha = 0.8f),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }

      IconButton(
        onClick = onNextMonth,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(AronaiNavy)
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
          contentDescription = "Next Month",
          tint = AronaiGold
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Quick "Today" Jump button and Date rule notice
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "★ Bodo Date is LARGE • English date is small",
        color = AronaiGoldLight,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )

      OutlinedButton(
        onClick = onGoToToday,
        modifier = Modifier.height(28.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = AronaiGold
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
          brush = androidx.compose.ui.graphics.SolidColor(AronaiGold)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Today,
          contentDescription = "Go to Today",
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Today (Din-Lir)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Weekdays Header in BODO (Deo, Som, Mon, Bud, Bir, Suk, Soni)
    Row(modifier = Modifier.fillMaxWidth()) {
      BodoSolarCalendar.WEEKDAYS_BODO_SHORT.forEachIndexed { idx, dayName ->
        Text(
          text = dayName,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          color = if (idx == 0) AronaiRed else AronaiGoldLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Calendar Grid Days with Horizontal Slide / Swipe Gesture Support (Sliding switches months seamlessly)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .pointerInput(displayedYear, displayedMonth) {
          detectHorizontalDragGestures(
            onDragStart = { dragDistanceX = 0f },
            onDragEnd = {
              if (dragDistanceX < -40f) {
                onNextMonth() // Slide Left -> Next Month
              } else if (dragDistanceX > 40f) {
                onPrevMonth() // Slide Right -> Previous Month
              }
              dragDistanceX = 0f
            },
            onDragCancel = { dragDistanceX = 0f },
            onHorizontalDrag = { change, dragAmount ->
              change.consume()
              dragDistanceX += dragAmount
            }
          )
        }
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Calculate leading empty spaces for the first day of month
        val firstDayOfWeek = if (monthDays.isNotEmpty()) {
          val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
          }
          cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun..6=Sat
        } else 0

        val totalCells = firstDayOfWeek + monthDays.size
        val numRows = (totalCells + 6) / 7

        for (row in 0 until numRows) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp)
          ) {
            for (col in 0 until 7) {
              val cellIndex = row * 7 + col
              val dayIndex = cellIndex - firstDayOfWeek

              if (dayIndex in monthDays.indices) {
                val bodoDate = monthDays[dayIndex]
                val isSelected = bodoDate.gregorianDay == selectedDate.gregorianDay &&
                    bodoDate.gregorianMonth == selectedDate.gregorianMonth &&
                    bodoDate.gregorianYear == selectedDate.gregorianYear
                val cellDateKey = String.format(java.util.Locale.ENGLISH, "%04d-%02d-%02d", bodoDate.gregorianYear, bodoDate.gregorianMonth, bodoDate.gregorianDay)
                val hasNote = datesWithNotes.contains(cellDateKey)

                BodoDayCell(
                  date = bodoDate,
                  isSelected = isSelected,
                  hasNote = hasNote,
                  onClick = { onDateSelected(bodoDate) },
                  modifier = Modifier.weight(1f)
                )
              } else {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }

    // Selected Date Details card
    Spacer(modifier = Modifier.height(10.dp))
    val selectedDateKey = String.format(java.util.Locale.ENGLISH, "%04d-%02d-%02d", selectedDate.gregorianYear, selectedDate.gregorianMonth, selectedDate.gregorianDay)
    val selectedHasNote = datesWithNotes.contains(selectedDateKey)
    SelectedDateDetailBanner(
      date = selectedDate,
      hasNote = selectedHasNote,
      onOpenNotes = { onOpenNotesForDate(selectedDate) }
    )
  }
}

/**
 * Calendar Cell with:
 * - BODO DATE LARGE (batha)
 * - ENGLISH DATE SMALL (chota)
 * - NOTE BADGE (📝)
 */
@Composable
fun BodoDayCell(
  date: BodoDate,
  isSelected: Boolean,
  hasNote: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isSunday = date.dayOfWeekEng == "Sunday" || date.dayOfWeekBodo == "Rabibar" || date.dayOfWeekBodo == "Deobar"

  Box(
    modifier = modifier
      .padding(2.dp)
      .aspectRatio(0.92f)
      .clip(RoundedCornerShape(8.dp))
      .background(
        when {
          isSelected -> AronaiGold.copy(alpha = 0.25f)
          date.isToday -> AronaiNavy
          else -> AronaiNavy.copy(alpha = 0.5f)
        }
      )
      .border(
        width = when {
          date.isToday -> 2.dp
          isSelected -> 1.5.dp
          else -> 0.5.dp
        },
        color = when {
          date.isToday -> AronaiGold
          isSelected -> AronaiGoldDark
          else -> AronaiSurfaceVariant.copy(alpha = 0.4f)
        },
        shape = RoundedCornerShape(8.dp)
      )
      .clickable { onClick() }
      .padding(3.dp)
  ) {
    // Note badge indicator in top-left
    if (hasNote) {
      Text(
        text = "📝",
        fontSize = 8.sp,
        modifier = Modifier.align(Alignment.TopStart)
      )
    }

    // English date in top-right corner (SMALL, CHOTA)
    Text(
      text = "${date.gregorianDay}",
      color = AronaiTextSecondary,
      fontSize = 9.sp,
      fontWeight = FontWeight.Medium,
      modifier = Modifier.align(Alignment.TopEnd)
    )

    // Bodo Solar Date in Center (LARGE, BATHA)
    Column(
      modifier = Modifier.align(Alignment.Center),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "${date.bodoDay}",
        color = when {
          date.isToday -> AronaiGold
          isSunday -> AronaiRed
          else -> Color.White
        },
        fontSize = 17.sp, // Bodo date is prominent and large!
        fontWeight = FontWeight.Black
      )
    }

    // Festival / Event indicator dot at bottom
    if (date.specialEvent != null) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .size(4.dp)
          .clip(CircleShape)
          .background(AronaiGold)
      )
    }
  }
}

@Composable
fun SelectedDateDetailBanner(
  date: BodoDate,
  hasNote: Boolean = false,
  onOpenNotes: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(AronaiNavy)
      .border(1.dp, AronaiGoldDark, RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "SELECTED DATE DETAILS",
          color = AronaiGoldLight,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Bodo date displayed prominently
          Text(
            text = "${date.bodoDay} ${date.bodoMonth.bodoName} (${date.bodoMonth.devanagariName})",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = "${date.dayOfWeekBodo} • Bodo San ${date.bodoYear} • ${date.tithi}",
          color = AronaiWarmWhite.copy(alpha = 0.8f),
          fontSize = 11.sp
        )
        if (date.specialEvent != null) {
          Text(
            text = "★ ${date.specialEvent}",
            color = AronaiGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "English Date",
          color = AronaiTextSecondary,
          fontSize = 9.sp
        )
        Text(
          text = "${date.gregorianDay}/${date.gregorianMonth}/${date.gregorianYear}",
          color = AronaiGoldLight,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = date.dayOfWeekEng,
          color = AronaiTextSecondary,
          fontSize = 10.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Notes Bar for selected date
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(if (hasNote) AronaiGold.copy(alpha = 0.18f) else AronaiSurface)
        .border(1.dp, if (hasNote) AronaiGold else AronaiGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
        .clickable { onOpenNotes() }
        .padding(horizontal = 12.dp, vertical = 9.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Text("📝", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = if (hasNote) "Is din par Note likha hua hai" else "Is din par Note ya Reminder likhein",
            color = if (hasNote) AronaiGold else AronaiWarmWhite,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (hasNote) "Dekhein, edit karein ya naya note jodein" else "Tap karein aur kisi bhi event ya kaam ka note likhein",
            color = AronaiGoldLight,
            fontSize = 10.sp
          )
        }
      }
      Spacer(modifier = Modifier.width(8.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(if (hasNote) AronaiGold else AronaiGreen)
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Text(
          text = if (hasNote) "Dekhein" else "+ Likhein",
          color = if (hasNote) AronaiNavy else Color.White,
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}
