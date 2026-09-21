package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun LiveHistoryTicker(
  messages: List<CulturalNewsItem>,
  currentIndex: Int,
  onDeleteMessage: (String) -> Unit,
  onOpenAdminPanel: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (messages.isEmpty()) {
    // Empty state when all messages dismissed
    Box(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(AronaiSurface)
        .padding(14.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "No new broadcast messages. Checking live feed...",
        color = AronaiTextSecondary,
        fontSize = 12.sp
      )
    }
    return
  }

  val safeIndex = currentIndex.coerceIn(0, messages.size - 1)
  val item = messages[safeIndex]
  var isExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(AronaiSurface)
      .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(listOf(AronaiRed, AronaiGold, AronaiGreen)),
        shape = RoundedCornerShape(14.dp)
      )
      .padding(12.dp)
  ) {
    // Top Bar of Ticker
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // News icon badge
        Box(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(if (item.isCustomAdmin) AronaiBadgeRed else AronaiGold),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (item.isCustomAdmin) Icons.Default.Campaign else Icons.Default.AutoAwesome,
            contentDescription = "Live Research Feed",
            tint = if (item.isCustomAdmin) Color.White else AronaiNavy,
            modifier = Modifier.size(15.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (item.isCustomAdmin) "ADMIN BROADCAST" else "SYSTEM RESEARCH (LIVE)",
          color = if (item.isCustomAdmin) AronaiBadgeRed else AronaiGold,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Category pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AronaiSurfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = item.category,
            color = AronaiWarmWhite,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Action buttons: Delete message (❌) and Carousel navigation
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Counter
        Text(
          text = "${safeIndex + 1}/${messages.size}",
          color = AronaiTextSecondary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(4.dp))

        // User Delete / Dismiss Message Button (as explicitly requested!)
        IconButton(
          onClick = { onDeleteMessage(item.id) },
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(AronaiBadgeRed.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Delete this message",
            tint = AronaiBadgeRed,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Message Title (English and Bodo)
    Text(
      text = item.title,
      color = Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      maxLines = if (isExpanded) Int.MAX_VALUE else 1,
      overflow = TextOverflow.Ellipsis
    )

    if (item.bodoTitle.isNotBlank()) {
      Text(
        text = "बड़ो: ${item.bodoTitle}",
        color = AronaiGoldLight,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Message Description (kya hua hai / research notes)
    Text(
      text = item.description,
      color = AronaiWarmWhite.copy(alpha = 0.9f),
      fontSize = 12.sp,
      lineHeight = 16.sp,
      maxLines = if (isExpanded) Int.MAX_VALUE else 2,
      overflow = TextOverflow.Ellipsis
    )

    if (isExpanded && item.bodoDescription.isNotBlank()) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Bodo: ${item.bodoDescription}",
        color = AronaiGoldLight.copy(alpha = 0.85f),
        fontSize = 11.sp,
        lineHeight = 15.sp
      )
    }

    // Bottom action row: Expand/Collapse & Admin action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (isExpanded) "Show Less ▲" else "Read More ▼",
        color = AronaiGold,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable { isExpanded = !isExpanded }
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Date: ${item.fullDate}",
          color = AronaiTextSecondary,
          fontSize = 10.sp
        )
      }
    }
  }
}
