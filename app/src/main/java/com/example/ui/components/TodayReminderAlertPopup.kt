package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DateNote
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
fun TodayReminderAlertPopup(
  todayDateDisplay: String, // e.g. "3 Aasin (आसिन) • 21 Sep 2026"
  notes: List<DateNote>,
  onAcknowledge: () -> Unit,
  onOpenNotesManager: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "bell_wobble")
  val bellAngle by infiniteTransition.animateFloat(
    initialValue = -18f,
    targetValue = 18f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bell_rotation"
  )

  Dialog(onDismissRequest = onAcknowledge) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(AronaiNavy)
        .border(2.dp, AronaiGold, RoundedCornerShape(24.dp))
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Animated Bell Icon with glowing background
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(AronaiGold.copy(alpha = 0.2f))
          .border(2.dp, AronaiGold, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = "Alert Sound Notification",
          tint = AronaiGold,
          modifier = Modifier
            .size(34.dp)
            .rotate(bellAngle)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Header Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(AronaiRed.copy(alpha = 0.2f))
          .border(1.dp, AronaiRed, RoundedCornerShape(20.dp))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = "🔔 AAPKA AAJ KA REMINDER ALERT",
          color = AronaiRed,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Aapka Aaj Ka Note Hai!",
        color = AronaiWarmWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center
      )

      Text(
        text = todayDateDisplay,
        color = AronaiGoldLight,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Notes Card Container
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 240.dp)
      ) {
        items(notes, key = { it.id }) { note ->
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(AronaiSurface)
              .border(1.dp, AronaiGoldDark, RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📌 ${note.title}",
                color = AronaiGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              if (note.isCompleted) {
                Text(
                  text = "✓ Pura Hua",
                  color = AronaiGreen,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            if (note.content.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = note.content,
                color = AronaiWarmWhite.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Action Buttons
      Button(
        onClick = onAcknowledge,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = AronaiGreen,
          contentColor = Color.White
        )
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Theek Hai / Dekh Liya",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedButton(
        onClick = onOpenNotesManager,
        modifier = Modifier
          .fillMaxWidth()
          .height(42.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = AronaiGold
        )
      ) {
        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Notes Manager Kholein",
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
      }
    }
  }
}
