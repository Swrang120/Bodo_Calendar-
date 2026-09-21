package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppUpdateInfo
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiGreen
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiRed
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiSurfaceVariant
import com.example.ui.theme.AronaiWarmWhite

@Composable
fun UpdateAvailableDialog(
  updateInfo: AppUpdateInfo,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = {
    if (!updateInfo.forceUpdate) onDismiss()
  }) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = AronaiSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, AronaiGold, RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AronaiGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = "Update Available",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = updateInfo.title,
                color = AronaiGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Version v${updateInfo.versionName} Available",
                color = AronaiGoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          if (!updateInfo.forceUpdate) {
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // What's New Box
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AronaiNavy)
            .border(1.dp, AronaiSurfaceVariant, RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Text(
            text = "⚡ What's New in this Update:",
            color = AronaiGoldLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = updateInfo.releaseNotes,
            color = AronaiWarmWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Download & Update Button
        ElevatedButton(
          onClick = {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.apkUrl))
            context.startActivity(browserIntent)
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = AronaiGreen,
            contentColor = Color.White
          )
        ) {
          Icon(imageVector = Icons.Default.Download, contentDescription = "Download Update", modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Download & Install Update",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        }

        if (!updateInfo.forceUpdate) {
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(text = "Later / Baad Mein Kare", color = AronaiWarmWhite.copy(alpha = 0.8f))
          }
        }
      }
    }
  }
}
