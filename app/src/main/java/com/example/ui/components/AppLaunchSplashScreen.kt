package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldDark
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiGreen
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiRed
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiWarmWhite
import com.example.util.AudioEffectManager
import kotlinx.coroutines.delay

@Composable
fun AppLaunchSplashScreen(
  onFinish: () -> Unit
) {
  var visible by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_splash")
  val glowScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_scale"
  )

  LaunchedEffect(Unit) {
    visible = true
    // Play warm cultural opening sound
    AudioEffectManager.playAppOpenSound()
    // Show opening animation for 2.3 seconds
    delay(2300L)
    onFinish()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            AronaiNavy,
            Color(0xFF0F1B2E),
            Color(0xFF070D18)
          )
        )
      )
      .clickable { onFinish() }, // allow immediate skip on tap
    contentAlignment = Alignment.Center
  ) {
    AnimatedVisibility(
      visible = visible,
      enter = fadeIn(tween(600)) + scaleIn(tween(600, easing = FastOutSlowInEasing)),
      exit = fadeOut(tween(400))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(0.88f)
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Glowing App Icon
        Box(
          modifier = Modifier
            .size(110.dp)
            .scale(glowScale)
            .clip(RoundedCornerShape(26.dp))
            .background(
              Brush.radialGradient(
                colors = listOf(
                  AronaiGold.copy(alpha = 0.4f),
                  Color.Transparent
                )
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(92.dp)
              .clip(RoundedCornerShape(22.dp))
              .background(AronaiGold)
              .border(2.5.dp, AronaiGoldLight, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_app_icon),
              contentDescription = "Bodo Calendar Emblem",
              modifier = Modifier.size(84.dp),
              contentScale = ContentScale.Crop
            )
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Cultural Greetings
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AronaiRed.copy(alpha = 0.25f))
            .border(1.dp, AronaiGold, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
          Text(
            text = "🌸 खुलुमबाय • KHULUMBAY 🌸",
            color = AronaiGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "BODO SAN CALENDAR",
          color = AronaiWarmWhite,
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.5.sp,
          textAlign = TextAlign.Center
        )

        Text(
          text = "बड़ो सान • Traditional Solar Calendar 2026",
          color = AronaiGoldLight,
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Bathou Heritage • History • Date Notes & Sound Reminders",
          color = Color(0xFFA0AEC0),
          fontSize = 10.5.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Loading and chime indicator
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AronaiSurface)
            .border(0.8.dp, AronaiGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("🔔", fontSize = 12.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Starting with sound chime...",
            color = AronaiGoldLight,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
