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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
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
fun AdminPanelDialog(
  onPublish: (title: String, bodoTitle: String, category: String, content: String, bodoContent: String, author: String) -> Unit,
  publishStatus: String?,
  onDismiss: () -> Unit
) {
  var title by remember { mutableStateOf("") }
  var bodoTitle by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("News") }
  var content by remember { mutableStateOf("") }
  var bodoContent by remember { mutableStateOf("") }
  var author by remember { mutableStateOf("Admin") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val categories = listOf("News", "History", "Puja", "Raja/Monarch", "Leader", "Festival")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = AronaiSurface,
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(1.5.dp, AronaiRed, RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = "Admin Broadcast",
              tint = AronaiBadgeRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "ADMIN MESSAGE BROADCAST",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "Supabase & Live App Display Sync",
                color = AronaiGoldLight,
                fontSize = 10.sp
              )
            }
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

        Spacer(modifier = Modifier.height(10.dp))

        // Status indicator if published
        if (publishStatus != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(AronaiGreen.copy(alpha = 0.2f))
              .border(1.dp, AronaiGreen, RoundedCornerShape(8.dp))
              .padding(8.dp)
          ) {
            Text(
              text = "✓ $publishStatus",
              color = AronaiGreen,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Category Selection
        Text(
          text = "Category (Kisko sambandhit hai):",
          color = AronaiGoldLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          categories.take(3).forEach { cat ->
            CategoryChip(
              name = cat,
              isSelected = category == cat,
              onClick = { category = cat }
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          categories.drop(3).forEach { cat ->
            CategoryChip(
              name = cat,
              isSelected = category == cat,
              onClick = { category = cat }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message Title Input
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Message Title (Ajj kya hua / Topic)", fontSize = 11.sp) },
          placeholder = { Text("e.g. Special Bodo Cultural Day Announcement", fontSize = 11.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bodo Title Input
        OutlinedTextField(
          value = bodoTitle,
          onValueChange = { bodoTitle = it },
          label = { Text("Bodo Title (बड़ो मुं)", fontSize = 11.sp) },
          placeholder = { Text("e.g. Bodo Harimu Gwnang San", fontSize = 11.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Content Input
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Details (Kya hua hai / News Description)", fontSize = 11.sp) },
          placeholder = { Text("Write full message details here to broadcast to user apps...", fontSize = 11.sp) },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth(),
          colors = customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Author Input
        OutlinedTextField(
          value = author,
          onValueChange = { author = it },
          label = { Text("Author / Sender (Likhne wala)", fontSize = 11.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = customTextFieldColors()
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = errorMessage!!,
            color = AronaiBadgeRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Publish Button
        ElevatedButton(
          onClick = {
            if (title.isBlank() || content.isBlank()) {
              errorMessage = "Title aur Details likhna zaroori hai!"
            } else {
              errorMessage = null
              onPublish(title, bodoTitle, category, content, bodoContent, author)
            }
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = AronaiRed,
            contentColor = Color.White
          )
        ) {
          Icon(imageVector = Icons.Default.Send, contentDescription = "Publish", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Broadcast Live Message", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun CategoryChip(
  name: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) AronaiGold else AronaiNavy)
      .border(
        width = 1.dp,
        color = if (isSelected) AronaiGoldDark else AronaiSurfaceVariant,
        shape = RoundedCornerShape(6.dp)
      )
      .padding(horizontal = 8.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = name,
      color = if (isSelected) AronaiNavy else AronaiWarmWhite,
      fontSize = 10.sp,
      fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
    )
  }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedBorderColor = AronaiGold,
  unfocusedBorderColor = AronaiSurfaceVariant,
  focusedTextColor = Color.White,
  unfocusedTextColor = AronaiWarmWhite,
  focusedLabelColor = AronaiGold,
  unfocusedLabelColor = AronaiTextSecondary,
  cursorColor = AronaiGold
)
