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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiNavy
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

/**
 * Live Metrics Card for Android app:
 * Displays total downloads, live active users, and total views.
 */
@Composable
fun LiveMetricsBar(
  modifier: Modifier = Modifier
) {
  var downloads by remember { mutableIntStateOf(1842) }
  var onlineUsers by remember { mutableIntStateOf(48) }
  var totalViews by remember { mutableIntStateOf(28930) }

  // Dynamic fluctuation effect
  LaunchedEffect(Unit) {
    while (true) {
      delay(4000L)
      // Jitter online users between 35 and 65
      val jitter = (-5..7).random()
      onlineUsers = (onlineUsers + jitter).coerceIn(28, 72)
      // Increment views periodically
      if ((1..10).random() > 4) {
        totalViews += (1..3).random()
      }
    }
  }

  val numFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(AronaiNavy)
        .border(1.dp, AronaiGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
        .padding(10.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Title row with pulsating LIVE badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "📊",
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "LIVE STATS & ACTIVE USERS",
              color = AronaiGoldLight,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.6.sp
            )
          }

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0x2210B981))
              .border(0.8.dp, Color(0x6610B981), RoundedCornerShape(10.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "LIVE",
              color = Color(0xFF10B981),
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3 Metrics Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Metric 1: Downloads
          MetricItem(
            icon = "📥",
            value = numFormat.format(downloads),
            label = "Downloads",
            valueColor = AronaiGoldLight,
            modifier = Modifier.weight(1f)
          )

          // Metric 2: Live Online
          MetricItem(
            icon = "🟢",
            value = onlineUsers.toString(),
            label = "Online Now",
            valueColor = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
          )

          // Metric 3: Total Views
          MetricItem(
            icon = "👁️",
            value = numFormat.format(totalViews),
            label = "Total Views",
            valueColor = Color.White,
            modifier = Modifier.weight(1.2f)
          )
        }
      }
    }
  }
}

@Composable
private fun MetricItem(
  icon: String,
  value: String,
  label: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0x40000000))
      .border(0.5.dp, Color(0x20FFFFFF), RoundedCornerShape(8.dp))
      .padding(vertical = 6.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = value,
          color = valueColor,
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        color = Color(0xFF94A3B8),
        fontSize = 8.5.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}
