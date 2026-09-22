package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.AronaiBadgeRed
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldDark
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiSurfaceVariant
import com.example.ui.theme.AronaiTextSecondary
import com.example.ui.theme.AronaiWarmWhite
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * AdMob Banner & Monetization Component.
 * Primary: Google AdMob Banner (Ad Unit: ca-app-pub-3120006032488545/7506284998)
 * Fallback: High-Fill Interactive Sponsor Banner & Direct Partner Monetization
 * (Guarantees ads always appear even when AdMob returns NO_FILL code 3 during unreleased testing).
 */
const val ADMOB_BANNER_AD_UNIT_ID = "ca-app-pub-3120006032488545/7506284998"
const val ADSTERRA_DIRECT_MONETIZATION_URL = "https://ensueddenied.com/nxd0j2dj?key=0a4d0cbe1cdd4f8562eafeb9e8dbcbb2"

@Composable
fun AdMobBanner(
  modifier: Modifier = Modifier,
  adUnitId: String = ADMOB_BANNER_AD_UNIT_ID
) {
  val context = LocalContext.current
  val isInEditMode = LocalInspectionMode.current

  var isAdMobLoaded by remember { mutableStateOf(false) }
  var adMobFailed by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Campaign,
          contentDescription = null,
          tint = AronaiGold,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "SPONSORED ADVERTISEMENT",
          color = AronaiGold.copy(alpha = 0.85f),
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
      }
      Text(
        text = if (isAdMobLoaded) "Google AdMob" else "Partner Network",
        color = AronaiTextSecondary,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(AronaiNavy)
        .border(1.dp, AronaiSurfaceVariant, RoundedCornerShape(12.dp)),
      contentAlignment = Alignment.Center
    ) {
      if (isInEditMode) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(Color(0xFF1E293B)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "AdMob Banner Preview (320x50)",
            color = Color.White,
            fontSize = 12.sp
          )
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Google AdMob AdView
          AndroidView(
            modifier = Modifier
              .fillMaxWidth()
              .height(if (isAdMobLoaded) 50.dp else 0.dp),
            factory = { ctx ->
              AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                this.adListener = object : AdListener() {
                  override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("AdMobBanner", "Google AdMob loaded successfully")
                    isAdMobLoaded = true
                    adMobFailed = false
                  }

                  override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.w("AdMobBanner", "AdMob failed (code=${loadAdError.code}): ${loadAdError.message}. Showing partner fallback.")
                    isAdMobLoaded = false
                    adMobFailed = true
                  }
                }
                loadAd(AdRequest.Builder().build())
              }
            }
          )

          // Fallback / Guaranteed Active Partner Ad (shown if AdMob is loading or failed with code 3 NO_FILL)
          if (!isAdMobLoaded) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      Color(0xFF1E1035),
                      Color(0xFF2D164D),
                      Color(0xFF190C2F)
                    )
                  )
                )
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ADSTERRA_DIRECT_MONETIZATION_URL))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    Log.e("AdMobBanner", "Cannot open ad link", e)
                  }
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(AronaiGold.copy(alpha = 0.2f))
                      .border(1.dp, AronaiGold, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Stars,
                      contentDescription = null,
                      tint = AronaiGold,
                      modifier = Modifier.size(20.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "Special Offers & Stories",
                        color = AronaiGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(AronaiBadgeRed)
                          .padding(horizontal = 4.dp, vertical = 1.dp)
                      ) {
                        Text(
                          text = "HOT",
                          color = Color.White,
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Black
                        )
                      }
                    }
                    Text(
                      text = "Tap to explore trending cultural news & rewards",
                      color = AronaiWarmWhite.copy(alpha = 0.85f),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Normal
                    )
                  }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AronaiGold)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "Visit",
                      color = Color.Black,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                      imageVector = Icons.Default.OpenInNew,
                      contentDescription = null,
                      tint = Color.Black,
                      modifier = Modifier.size(12.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
