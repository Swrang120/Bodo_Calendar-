package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BodoDate
import com.example.data.CulturalNewsItem
import com.example.ui.theme.AronaiBadgeRed
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

@Composable
fun ThreeDayForecastCard(
  todayDate: BodoDate,
  tomorrowDate: BodoDate,
  dayAfterDate: BodoDate,
  todayEvents: List<CulturalNewsItem>,
  tomorrowEvents: List<CulturalNewsItem>,
  dayAfterEvents: List<CulturalNewsItem>,
  onDeleteEvent: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) } // 0 = Today, 1 = Tomorrow, 2 = Day After

  val currentBodoDate = when (selectedTab) {
    0 -> todayDate
    1 -> tomorrowDate
    else -> dayAfterDate
  }

  val currentEvents = when (selectedTab) {
    0 -> todayEvents
    1 -> tomorrowEvents
    else -> dayAfterEvents
  }

  val tabTitleBodo = when (selectedTab) {
    0 -> "Din-Lir (Ajj)"
    1 -> "Gabun (Kal)"
    else -> "Sampoi (Parson)"
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(AronaiSurface)
      .border(1.dp, AronaiSurfaceVariant, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    // Header title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Event,
          contentDescription = "Solar Calendar Sync",
          tint = AronaiGold,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ASSAMESE/BODO SOLAR SYNC",
          color = AronaiGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
      }

      Text(
        text = "English & Bodo (No Assamese Text)",
        color = AronaiTextSecondary,
        fontSize = 9.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Three Tab Selectors: Today / Tomorrow / Day After
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(AronaiNavy)
        .padding(3.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      ForecastTabButton(
        title = "Ajj (Today)",
        subTitle = "Din-Lir",
        isSelected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        modifier = Modifier.weight(1f)
      )
      Spacer(modifier = Modifier.width(4.dp))
      ForecastTabButton(
        title = "Kal (Tomorrow)",
        subTitle = "Gabun",
        isSelected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        modifier = Modifier.weight(1f)
      )
      Spacer(modifier = Modifier.width(4.dp))
      ForecastTabButton(
        title = "Parson (Day After)",
        subTitle = "Sampoi",
        isSelected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Active Date Badge: BODO DATE LARGE, ENGLISH DATE SMALL!
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(AronaiNavy)
        .border(1.dp, AronaiGoldDark, RoundedCornerShape(12.dp))
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "$tabTitleBodo Solar Date",
          color = AronaiGoldLight,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          // BODO DATE IS LARGE AND PROMINENT
          Text(
            text = "${currentBodoDate.bodoDay} ${currentBodoDate.bodoMonth.bodoName}",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = " (${currentBodoDate.bodoMonth.devanagariName})",
            color = AronaiGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = "${currentBodoDate.dayOfWeekBodo} • Bodo San ${currentBodoDate.bodoYear}",
          color = AronaiWarmWhite.copy(alpha = 0.8f),
          fontSize = 11.sp
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        // ENGLISH DATE IS SMALL
        Text(
          text = "English",
          color = AronaiTextSecondary,
          fontSize = 9.sp
        )
        Text(
          text = "${currentBodoDate.gregorianDay}/${currentBodoDate.gregorianMonth}/${currentBodoDate.gregorianYear}",
          color = AronaiGoldLight,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = currentBodoDate.dayOfWeekEng,
          color = AronaiTextSecondary,
          fontSize = 10.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Research & Events for selected day (kings, puja, martyrs)
    Text(
      text = "KAUN SA KYA HAI / AJJ KYA HUA HAI:",
      color = AronaiGoldLight,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    if (currentEvents.isEmpty()) {
      Text(
        text = "No recorded conflicts or events for this date. Traditional Bathou peace prevails.",
        color = AronaiTextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 4.dp)
      )
    } else {
      currentEvents.forEach { ev ->
        EventCardItem(
          item = ev,
          onDelete = { onDeleteEvent(ev.id) }
        )
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}

@Composable
private fun ForecastTabButton(
  title: String,
  subTitle: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) AronaiGold else Color.Transparent)
      .clickable { onClick() }
      .padding(vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = title,
        color = if (isSelected) AronaiNavy else AronaiWarmWhite,
        fontSize = 10.sp,
        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
      )
      Text(
        text = subTitle,
        color = if (isSelected) AronaiNavy.copy(alpha = 0.8f) else AronaiTextSecondary,
        fontSize = 9.sp
      )
    }
  }
}

@Composable
private fun EventCardItem(
  item: CulturalNewsItem,
  onDelete: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(AronaiNavy)
      .padding(10.dp),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(modifier = Modifier.weight(1f)) {
      Icon(
        imageVector = if (item.category.contains("Puja")) Icons.Default.TempleHindu else Icons.Default.HistoryEdu,
        contentDescription = item.category,
        tint = if (item.category.contains("Puja")) AronaiGreen else AronaiGold,
        modifier = Modifier
          .size(18.dp)
          .padding(top = 2.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
        if (item.bodoTitle.isNotBlank()) {
          Text(
            text = "बड़ो: ${item.bodoTitle}",
            color = AronaiGoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = item.description,
          color = AronaiWarmWhite.copy(alpha = 0.85f),
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }
    }

    // Dismiss / Delete action button
    IconButton(
      onClick = onDelete,
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(AronaiSurfaceVariant)
    ) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Delete event",
        tint = AronaiBadgeRed,
        modifier = Modifier.size(13.dp)
      )
    }
  }
}
