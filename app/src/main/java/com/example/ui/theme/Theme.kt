package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AronaiDarkColorScheme = darkColorScheme(
  primary = AronaiGold,
  onPrimary = AronaiNavy,
  primaryContainer = AronaiGoldDark,
  onPrimaryContainer = AronaiWarmWhite,
  secondary = AronaiRed,
  onSecondary = Color.White,
  secondaryContainer = AronaiRedDark,
  onSecondaryContainer = AronaiWarmWhite,
  tertiary = AronaiGreen,
  onTertiary = Color.White,
  background = AronaiNavy,
  onBackground = AronaiWarmWhite,
  surface = AronaiSurface,
  onSurface = AronaiWarmWhite,
  surfaceVariant = AronaiSurfaceVariant,
  onSurfaceVariant = AronaiTextSecondary
)

private val AronaiLightColorScheme = lightColorScheme(
  primary = AronaiGoldDark,
  onPrimary = Color.White,
  primaryContainer = AronaiGoldLight,
  onPrimaryContainer = AronaiNavy,
  secondary = AronaiRed,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFCDD2),
  onSecondaryContainer = AronaiRedDark,
  tertiary = AronaiGreen,
  onTertiary = Color.White,
  background = Color(0xFFFBF8F2),
  onBackground = Color(0xFF1E293B),
  surface = Color.White,
  onSurface = Color(0xFF1E293B),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to deep Aronai dark theme for rich contrast and vibrant colors
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) AronaiDarkColorScheme else AronaiLightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

