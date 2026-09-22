package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BodoDate
import com.example.ui.theme.AronaiBadgeRed
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiGreen
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiRed
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiWarmWhite

@Composable
fun AronaiHeader(
  todayDate: BodoDate,
  currentRunningMonthEng: String,
  onOpenMonthInfo: () -> Unit,
  onOpenAdminPanel: () -> Unit,
  onOpenAppDownload: () -> Unit,
  onOpenSidebar: () -> Unit = {},
  onOpenNotes: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    // Top Bar with cultural branding and action icons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(AronaiNavy)
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f, fill = false)
      ) {
        // Hamburger Menu button (Opens Sidebar Drawer) - Identical to website button
        IconButton(
          onClick = onOpenSidebar,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AronaiSurface)
            .border(1.2.dp, AronaiGold, RoundedCornerShape(10.dp))
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Open Sidebar Menu",
            tint = AronaiGold,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Aronai motif badge
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(AronaiGold)
            .border(1.5.dp, AronaiRed, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_app_icon),
            contentDescription = "Bodo Calendar Icon",
            modifier = Modifier.size(34.dp),
            contentScale = ContentScale.Crop
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "BODO SAN CALENDAR",
            color = AronaiGold,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "बड़ो सान • Traditional Solar Calendar",
            color = AronaiWarmWhite.copy(alpha = 0.85f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Quick Action Buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Notes & Reminders button (Prominent pill button)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AronaiGold.copy(alpha = 0.2f))
            .border(1.2.dp, AronaiGold, RoundedCornerShape(12.dp))
            .clickable(onClick = onOpenNotes)
            .padding(horizontal = 7.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.EditNote,
              contentDescription = "Notes & Reminders",
              tint = AronaiGold,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Notes",
              color = AronaiGold,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Month Explanation Info button
        IconButton(
          onClick = onOpenMonthInfo,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AronaiSurface)
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Current Month Info",
            tint = AronaiGoldLight,
            modifier = Modifier.size(19.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Download App button
        IconButton(
          onClick = onOpenAppDownload,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AronaiGreen.copy(alpha = 0.85f))
        ) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download App Option",
            tint = Color.White,
            modifier = Modifier.size(19.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Admin Panel button
        IconButton(
          onClick = onOpenAdminPanel,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AronaiRed)
        ) {
          Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Admin Panel Broadcast",
            tint = Color.White,
            modifier = Modifier.size(19.dp)
          )
        }
      }
    }

    // Hero Card Featuring Bagurumba Dance Photo & Aronai Motif
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .clip(RoundedCornerShape(16.dp))
        .border(2.dp, AronaiGoldDarkBorder(), RoundedCornerShape(16.dp))
    ) {
      // Photo of Bagurumba Bodo dancers
      Image(
        painter = painterResource(id = R.drawable.img_bagurumba),
        contentDescription = "Bodo Bagurumba Dance & Aronai Tradition",
        modifier = Modifier
          .fillMaxWidth()
          .height(148.dp),
        contentScale = ContentScale.Crop
      )

      // Gradient overlay for contrast
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(148.dp)
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Transparent,
                Color(0xBB0F172A),
                Color(0xF00F172A)
              )
            )
          )
      )

      // Foreground Cultural Banner content
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(148.dp)
          .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top row: Live indicator & Midnight rollover badge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Live status badge
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(AronaiBadgeRed.copy(alpha = 0.9f))
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color.White)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "LIVE SYSTEM",
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Midnight auto-change guarantee badge
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(Color.Black.copy(alpha = 0.65f))
              .border(1.dp, AronaiGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = "Midnight Rollover",
              tint = AronaiGold,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "12:00 AM Auto-Rollover Active",
              color = AronaiWarmWhite,
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Bottom banner: Prominent Today Date in Bodo & English
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text(
              text = "AJJ KA DIN / DIN-LIR",
              color = AronaiGoldLight,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              // BODO DATE (Prominent & Large)
              Text(
                text = "${todayDate.bodoDay} ${todayDate.bodoMonth.bodoName}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = " (${todayDate.bodoMonth.devanagariName})",
                color = AronaiGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "Bodo San ${todayDate.bodoYear} • ${todayDate.bodoMonth.seasonBodo} Ritu",
              color = AronaiWarmWhite.copy(alpha = 0.85f),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // English month comparison tag
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(AronaiNavy.copy(alpha = 0.85f))
              .border(1.dp, AronaiGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
              .clickable { onOpenMonthInfo() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "English Date",
              color = AronaiGoldLight,
              fontSize = 9.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "${todayDate.gregorianDay} $currentRunningMonthEng",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = todayDate.dayOfWeekEng,
              color = AronaiGold,
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AronaiGoldDarkBorder(): Brush {
  return Brush.linearGradient(
    colors = listOf(AronaiGold, AronaiRed, AronaiGold)
  )
}
