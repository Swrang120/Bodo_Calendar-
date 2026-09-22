package com.example.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun DateNotesDialog(
  dateKey: String, // "yyyy-MM-dd"
  dateDisplayTitle: String, // e.g. "3 Aasin (आसिन) • 21 Sep 2026"
  notes: List<DateNote>,
  onAddNote: (title: String, content: String) -> Unit,
  onDeleteNote: (id: String) -> Unit,
  onToggleComplete: (id: String) -> Unit,
  onDismiss: () -> Unit
) {
  var newTitle by remember { mutableStateOf("") }
  var newContent by remember { mutableStateOf("") }
  var isAddingNew by remember { mutableStateOf(notes.isEmpty()) }

  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(AronaiNavy)
        .border(1.5.dp, AronaiGold, RoundedCornerShape(20.dp))
        .padding(18.dp)
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
              .background(AronaiGold),
            contentAlignment = Alignment.Center
          ) {
            Text("📝", fontSize = 18.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "DATE NOTES & REMINDERS",
              color = AronaiGoldLight,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
            Text(
              text = dateDisplayTitle,
              color = AronaiWarmWhite,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(AronaiSurfaceVariant)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = AronaiWarmWhite,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Informative Auto-Reminder Badge
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(AronaiSurface)
          .border(0.8.dp, AronaiGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = null,
          tint = AronaiGold,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Is din aane par system automatically pop-up & sound ke saath aapko notify karega!",
          color = AronaiGoldLight,
          fontSize = 11.sp,
          lineHeight = 15.sp
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Existing Notes list
      if (notes.isNotEmpty()) {
        Text(
          text = "SAVED NOTES (${notes.size})",
          color = AronaiTextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
        ) {
          items(notes, key = { it.id }) { note ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AronaiSurface)
                .border(0.6.dp, AronaiSurfaceVariant, RoundedCornerShape(10.dp))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              IconButton(
                onClick = { onToggleComplete(note.id) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = if (note.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                  contentDescription = "Toggle Complete",
                  tint = if (note.isCompleted) AronaiGreen else AronaiGold
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = note.title,
                  color = if (note.isCompleted) AronaiTextSecondary else AronaiWarmWhite,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                if (note.content.isNotBlank()) {
                  Text(
                    text = note.content,
                    color = AronaiTextSecondary,
                    fontSize = 11.sp
                  )
                }
              }

              IconButton(
                onClick = { onDeleteNote(note.id) },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Delete",
                  tint = AronaiRed.copy(alpha = 0.8f),
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
      }

      // Add New Note section
      if (isAddingNew || notes.isEmpty()) {
        Text(
          text = "NAYA NOTE LIKHEIN",
          color = AronaiGold,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = newTitle,
          onValueChange = { newTitle = it },
          placeholder = { Text("Note Title (e.g. Bathou Pooja, Meeting, Event)", fontSize = 12.sp, color = AronaiTextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AronaiGold,
            unfocusedBorderColor = AronaiSurfaceVariant,
            focusedTextColor = AronaiWarmWhite,
            unfocusedTextColor = AronaiWarmWhite,
            focusedContainerColor = AronaiSurface,
            unfocusedContainerColor = AronaiSurface
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = newContent,
          onValueChange = { newContent = it },
          placeholder = { Text("Details / Note description (Optional)", fontSize = 12.sp, color = AronaiTextSecondary) },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AronaiGold,
            unfocusedBorderColor = AronaiSurfaceVariant,
            focusedTextColor = AronaiWarmWhite,
            unfocusedTextColor = AronaiWarmWhite,
            focusedContainerColor = AronaiSurface,
            unfocusedContainerColor = AronaiSurface
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = {
            if (newTitle.isNotBlank()) {
              onAddNote(newTitle, newContent)
              newTitle = ""
              newContent = ""
              isAddingNew = false
            }
          },
          enabled = newTitle.isNotBlank(),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AronaiGreen,
            contentColor = Color.White
          )
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Note Save Karein", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      } else {
        Button(
          onClick = { isAddingNew = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AronaiSurface,
            contentColor = AronaiGold
          )
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("+ Naya Note Add Karein", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

/**
 * Dialog displaying overview of all user notes and reminders across all dates
 */
@Composable
fun AllNotesListDialog(
  allNotes: List<DateNote>,
  onOpenDateNotes: (dateKey: String, dateDisplay: String) -> Unit,
  onAddNewNote: () -> Unit,
  onDismiss: () -> Unit
) {
  val notesByDate = allNotes.groupBy { it.dateKey }
  val totalNotesCount = allNotes.size

  Dialog(onDismissRequest = onDismiss) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .background(AronaiNavy)
        .border(1.2.dp, AronaiGold, RoundedCornerShape(20.dp))
        .padding(18.dp)
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
              .size(38.dp)
              .clip(CircleShape)
              .background(AronaiGold.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text("📝", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Mere Notes & Reminders",
              color = AronaiWarmWhite,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (totalNotesCount > 0) "$totalNotesCount notes saved • Sound alert ready" else "Date par likhein, us din pop-up payein",
              color = AronaiGoldLight,
              fontSize = 11.sp
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AronaiSurface)
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

      if (notesByDate.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Abhi koi Note ya Reminder nahi hai.",
            color = AronaiTextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Aap kisi bhi date par note likh kar rakh sakte hain.\nUs din par calendar automatically notification aur sound alert dega!",
            color = AronaiWarmWhite.copy(alpha = 0.8f),
            fontSize = 11.5.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onAddNewNote,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = AronaiGold,
              contentColor = AronaiNavy
            )
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Note Likhein", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 380.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(notesByDate.entries.toList(), key = { it.key }) { entry ->
            val dateKey = entry.key
            val notes = entry.value

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AronaiSurface)
                .border(0.8.dp, AronaiSurfaceVariant, RoundedCornerShape(12.dp))
                .clickable { onOpenDateNotes(dateKey, dateKey) }
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = AronaiGold,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Date: $dateKey",
                    color = AronaiGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                Text(
                  text = "Dekhein / Edit ›",
                  color = AronaiGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              notes.forEach { note ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("•", color = AronaiGold, fontSize = 12.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = note.title,
                    color = AronaiWarmWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                  if (note.content.isNotBlank()) {
                    Text(
                      text = " - ${note.content}",
                      color = AronaiTextSecondary,
                      fontSize = 11.sp,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onAddNewNote,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = AronaiGold,
            contentColor = AronaiNavy
          )
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("+ Naya Note Likhein", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
