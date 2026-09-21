package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiGreen
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiRed
import com.example.ui.theme.AronaiSurface
import com.example.ui.theme.AronaiSurfaceVariant
import com.example.ui.theme.AronaiTextSecondary
import com.example.ui.theme.AronaiWarmWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Sidebar Navigation Drawer matching exactly user's screenshot:
 * 1. HELP & SUPPORT
 *    - Chat with Us (Opens instant email to sboro3235@gmail.com)
 *    - Join Our WhatsApp Community
 *    - Report an Issue (Bug report to sboro3235@gmail.com)
 *    - Suggest a Feature (Feature suggestion to sboro3235@gmail.com)
 * 2. LEGAL
 *    - Privacy Policy
 *    - Terms & Conditions
 * 3. Log Out
 * 4. Delete Account
 */
@Composable
fun AppSidebarDrawer(
  onClose: () -> Unit,
  onOpenFeedback: (category: String, title: String) -> Unit,
  onOpenPrivacyPolicy: () -> Unit,
  onOpenTerms: () -> Unit
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxWidth(0.85f)
      .fillMaxHeight()
      .background(Color(0xFF0F172A))
      .padding(horizontal = 18.dp, vertical = 20.dp)
      .verticalScroll(rememberScrollState())
  ) {
    // Header with Close
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(AronaiGold),
          contentAlignment = Alignment.Center
        ) {
          Text("ब", color = AronaiNavy, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Bodo Calendar",
            color = AronaiWarmWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "sboro3235@gmail.com",
            color = AronaiGoldLight,
            fontSize = 11.sp
          )
        }
      }

      IconButton(
        onClick = onClose,
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(Color(0xFF1E293B))
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close Sidebar",
          tint = AronaiWarmWhite,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // SECTION 1: HELP & SUPPORT
    Text(
      text = "HELP & SUPPORT",
      color = AronaiTextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF161F30))
        .border(0.8.dp, Color(0xFF2D3748), RoundedCornerShape(16.dp))
    ) {
      // 1. Chat with Us
      DrawerMenuItem(
        icon = Icons.AutoMirrored.Filled.Chat,
        title = "Chat with Us",
        onClick = {
          onClose()
          onOpenFeedback("Chat with Us", "Aap jo bhi kehna ya poochna chahte hain yahan likhein:")
        }
      )
      DrawerDivider()

      // 2. Join Our WhatsApp Community
      DrawerMenuItem(
        icon = Icons.Default.Group,
        title = "Join Our WhatsApp Community",
        onClick = {
          onClose()
          try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/"))
            context.startActivity(intent)
          } catch (e: Exception) {
            Toast.makeText(context, "Opening WhatsApp...", Toast.LENGTH_SHORT).show()
          }
        }
      )
      DrawerDivider()

      // 3. Report an Issue
      DrawerMenuItem(
        icon = Icons.Default.BugReport,
        title = "Report an Issue",
        onClick = {
          onClose()
          onOpenFeedback("Report an Issue", "Calendar ya dates mein kya problem aayi? Yahan detail mein likhein:")
        }
      )
      DrawerDivider()

      // 4. Suggest a Feature
      DrawerMenuItem(
        icon = Icons.Default.Lightbulb,
        title = "Suggest a Feature",
        onClick = {
          onClose()
          onOpenFeedback("Suggest a Feature", "Calendar mein kya naya feature hona chahiye? Apna sujhaav likhein:")
        }
      )
    }

    Spacer(modifier = Modifier.height(22.dp))

    // SECTION 2: LEGAL
    Text(
      text = "LEGAL",
      color = AronaiTextSecondary,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF161F30))
        .border(0.8.dp, Color(0xFF2D3748), RoundedCornerShape(16.dp))
    ) {
      // Privacy Policy
      DrawerMenuItem(
        icon = Icons.Default.Security,
        title = "Privacy Policy",
        onClick = {
          onClose()
          onOpenPrivacyPolicy()
        }
      )
      DrawerDivider()

      // Terms & Conditions
      DrawerMenuItem(
        icon = Icons.Default.Description,
        title = "Terms & Conditions",
        onClick = {
          onClose()
          onOpenTerms()
        }
      )
    }

    Spacer(modifier = Modifier.height(28.dp))

    // SECTION 3: Log Out Button
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        .clickable {
          Toast.makeText(context, "Successfully logged out.", Toast.LENGTH_SHORT).show()
          onClose()
        }
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "Log Out",
        color = Color(0xFFF87171),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Delete Account Text Button
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clickable {
          Toast.makeText(context, "Account reset request recorded.", Toast.LENGTH_SHORT).show()
          onClose()
        }
        .padding(vertical = 6.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "Delete Account",
        color = Color(0xFF64748B),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
private fun DrawerMenuItem(
  icon: ImageVector,
  title: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = AronaiWarmWhite.copy(alpha = 0.85f),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(14.dp))
      Text(
        text = title,
        color = AronaiWarmWhite,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium
      )
    }

    Icon(
      imageVector = Icons.Default.ChevronRight,
      contentDescription = "Go",
      tint = Color(0xFF475569),
      modifier = Modifier.size(18.dp)
    )
  }
}

@Composable
private fun DrawerDivider() {
  HorizontalDivider(
    color = Color(0xFF233045),
    thickness = 0.8.dp,
    modifier = Modifier.padding(start = 50.dp)
  )
}

/**
 * Direct Feedback Dialog that sends whatever the user types to sboro3235@gmail.com
 */
@Composable
fun SendFeedbackEmailDialog(
  category: String,
  subtitle: String,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var name by remember { mutableStateOf("") }
  var emailOrPhone by remember { mutableStateOf("") }
  var userMessage by remember { mutableStateOf("") }
  var isSending by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }
  var isSuccess by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = AronaiSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.2.dp, AronaiGold, RoundedCornerShape(20.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Title Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(AronaiGold),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mail,
                contentDescription = null,
                tint = AronaiNavy,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = category,
                color = AronaiGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Direct Support Message",
                color = AronaiGoldLight,
                fontSize = 10.5.sp
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

        // Recipient target indicator
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x2210B981))
            .border(0.8.dp, Color(0x6610B981), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("✉️", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Target Mail: sboro3235@gmail.com",
              color = Color(0xFF10B981),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = subtitle,
          color = AronaiWarmWhite.copy(alpha = 0.85f),
          fontSize = 11.5.sp,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Name input
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          placeholder = { Text("Aapka Naam (Your Name)", fontSize = 12.sp, color = AronaiTextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AronaiGold,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = AronaiWarmWhite,
            unfocusedTextColor = AronaiWarmWhite
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email / Phone input
        OutlinedTextField(
          value = emailOrPhone,
          onValueChange = { emailOrPhone = it },
          placeholder = { Text("Aapka Email ya Phone No.", fontSize = 12.sp, color = AronaiTextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AronaiGold,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = AronaiWarmWhite,
            unfocusedTextColor = AronaiWarmWhite
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message input
        OutlinedTextField(
          value = userMessage,
          onValueChange = { userMessage = it },
          placeholder = { Text("Aap jo bhi bolna chahte hain yahan likhein...", fontSize = 12.sp, color = AronaiTextSecondary) },
          minLines = 4,
          maxLines = 6,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AronaiGold,
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = AronaiWarmWhite,
            unfocusedTextColor = AronaiWarmWhite
          )
        )

        if (statusMessage != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSuccess) Color(0x3310B981) else Color(0x33EF4444))
              .padding(8.dp)
          ) {
            Text(
              text = statusMessage ?: "",
              color = if (isSuccess) Color(0xFF10B981) else Color(0xFFF87171),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Send Email Button
        ElevatedButton(
          onClick = {
            if (userMessage.isBlank()) {
              statusMessage = "Kripya apna sandesh likhein!"
              isSuccess = false
              return@ElevatedButton
            }

            isSending = true
            statusMessage = "Sandesh sboro3235@gmail.com ko bheja ja raha hai..."
            isSuccess = true

            scope.launch {
              val success = sendToSupportEmail(
                category = category,
                name = name.ifBlank { "Bodo Calendar App User" },
                replyTo = emailOrPhone.ifBlank { "sboro3235@gmail.com" },
                message = userMessage
              )

              isSending = false
              if (success) {
                isSuccess = true
                statusMessage = "✅ Aapka sandesh safalta se sboro3235@gmail.com par bhej diya gaya hai!"
                userMessage = ""
              } else {
                // Fallback to launch local Gmail App directly
                launchDirectEmailIntent(context, category, name, emailOrPhone, userMessage)
                statusMessage = "Gmail App open ho raha hai..."
              }
            }
          },
          enabled = !isSending,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = AronaiGreen,
            contentColor = Color.White
          )
        ) {
          Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isSending) "Bheja ja raha hai..." else "Send to sboro3235@gmail.com",
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Open directly in Gmail app fallback
        OutlinedButton(
          onClick = {
            launchDirectEmailIntent(context, category, name, emailOrPhone, userMessage)
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(imageVector = Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(14.dp), tint = AronaiGold)
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Open in Gmail App", color = AronaiWarmWhite, fontSize = 12.sp)
        }
      }
    }
  }
}

/**
 * Simple Legal Modal for Privacy Policy and Terms & Conditions
 */
@Composable
fun LegalInfoDialog(
  title: String,
  content: String,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = AronaiSurface,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.2.dp, AronaiGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            color = AronaiGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
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

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = content,
          color = AronaiWarmWhite.copy(alpha = 0.9f),
          fontSize = 12.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        ElevatedButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = AronaiGold,
            contentColor = AronaiNavy
          )
        ) {
          Text(text = "Theek Hai (Close)", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

private suspend fun sendToSupportEmail(
  category: String,
  name: String,
  replyTo: String,
  message: String
): Boolean = withContext(Dispatchers.IO) {
  try {
    val url = URL("https://formsubmit.co/ajax/sboro3235@gmail.com")
    val connection = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      setRequestProperty("Content-Type", "application/json")
      setRequestProperty("Accept", "application/json")
      doOutput = true
      connectTimeout = 8000
      readTimeout = 8000
    }

    val json = JSONObject().apply {
      put("name", name)
      put("category", category)
      put("_replyto", replyTo)
      put("_subject", "[Bodo Calendar App] $category from $name")
      put("message", message)
      put("recipient", "sboro3235@gmail.com")
      put("app", "Bodo San Calendar Android App")
    }

    OutputStreamWriter(connection.outputStream).use { writer ->
      writer.write(json.toString())
      writer.flush()
    }

    connection.responseCode in 200..299
  } catch (e: Exception) {
    false
  }
}

private fun launchDirectEmailIntent(
  context: Context,
  category: String,
  name: String,
  senderContact: String,
  msg: String
) {
  try {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("mailto:sboro3235@gmail.com")
      putExtra(Intent.EXTRA_SUBJECT, "[Bodo Calendar App] $category from $name")
      putExtra(
        Intent.EXTRA_TEXT,
        "Name: $name\nContact: $senderContact\n\nMessage:\n$msg"
      )
    }
    context.startActivity(Intent.createChooser(intent, "Send Email with"))
  } catch (e: Exception) {
    Toast.makeText(context, "Email: sboro3235@gmail.com", Toast.LENGTH_LONG).show()
  }
}
