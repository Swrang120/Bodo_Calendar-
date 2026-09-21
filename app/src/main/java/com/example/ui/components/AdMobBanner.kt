package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiNavy
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * AdMob Banner Ad Component.
 * Uses the user's Banner Ad Unit ID: ca-app-pub-3120006032488545/7506284998
 */
const val ADMOB_BANNER_AD_UNIT_ID = "ca-app-pub-3120006032488545/7506284998"

@Composable
fun AdMobBanner(
  modifier: Modifier = Modifier,
  adUnitId: String = ADMOB_BANNER_AD_UNIT_ID
) {
  val isInEditMode = LocalInspectionMode.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "SPONSORED ADVERTISEMENT",
      color = AronaiGold.copy(alpha = 0.7f),
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.8.sp
    )

    Spacer(modifier = Modifier.height(3.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(AronaiNavy)
        .border(1.dp, Color(0x33F59E0B), RoundedCornerShape(8.dp))
        .padding(vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isInEditMode) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFF1E293B)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Google AdMob Banner Preview (320x50)",
            color = Color.White,
            fontSize = 12.sp
          )
        }
      } else {
        AndroidView(
          modifier = Modifier.fillMaxWidth(),
          factory = { context ->
            AdView(context).apply {
              setAdSize(AdSize.BANNER)
              this.adUnitId = adUnitId
              loadAd(AdRequest.Builder().build())
            }
          }
        )
      }
    }
  }
}
