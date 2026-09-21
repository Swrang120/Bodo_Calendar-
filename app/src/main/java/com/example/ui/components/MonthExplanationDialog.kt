package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BodoDate
import com.example.data.BodoMonth
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldDark
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiSurfaceVariant
import com.example.ui.theme.AronaiTextSecondary
import com.example.ui.theme.AronaiWarmWhite

@Composable
fun MonthExplanationDialog(
  todayDate: BodoDate,
  currentRunningMonthEng: String,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = AronaiSurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(1.5.dp, AronaiGold, RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Title row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = "Month Info",
              tint = AronaiGold,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "BODO & ENGLISH MONTHS",
              color = AronaiGold,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(AronaiNavy)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = AronaiWarmWhite,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Month Focus Card (Direct Answer to User Query)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AronaiNavy)
            .border(1.dp, AronaiGoldDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Text(
            text = "ABHI KAUN SA MAHINA CHAL RAHA HAI?",
            color = AronaiGoldLight,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "• English Month: ",
              color = AronaiTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = currentRunningMonthEng,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "• Bodo Solar Month: ",
              color = AronaiTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "${todayDate.bodoMonth.bodoName} (${todayDate.bodoMonth.devanagariName})",
              color = AronaiGold,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "• Bodo Solar Date: ",
              color = AronaiTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Day ${todayDate.bodoDay}, Bodo San ${todayDate.bodoYear}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "• Season / Ritu: ",
              color = AronaiTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "${todayDate.bodoMonth.seasonBodo} (${todayDate.bodoMonth.seasonEnglish})",
              color = AronaiWarmWhite,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "ALL 12 BODO SOLAR MONTHS (बड़ो 12 महिना):",
          color = AronaiGoldLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Scrollable list of all 12 Bodo months
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
        ) {
          items(BodoMonth.entries) { month ->
            val isCurrent = month == todayDate.bodoMonth
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrent) AronaiGold.copy(alpha = 0.2f) else AronaiNavy)
                .border(
                  width = if (isCurrent) 1.dp else 0.5.dp,
                  color = if (isCurrent) AronaiGold else AronaiSurfaceVariant,
                  shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Current Month",
                    tint = AronaiGold,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                  text = "${month.index}. ${month.bodoName} (${month.devanagariName})",
                  color = if (isCurrent) AronaiGold else Color.White,
                  fontSize = 12.sp,
                  fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = month.englishSpan,
                  color = AronaiWarmWhite.copy(alpha = 0.85f),
                  fontSize = 10.sp
                )
                Text(
                  text = month.seasonBodo,
                  color = AronaiTextSecondary,
                  fontSize = 9.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ElevatedButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = AronaiGold,
            contentColor = AronaiNavy
          )
        ) {
          Text(text = "Samajh Gaya / Understood", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
