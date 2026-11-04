package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkFantasyColorScheme = darkColorScheme(
  primary = ArcaneCyan,
  onPrimary = DeepAbyss,
  primaryContainer = ArcaneBlue,
  onPrimaryContainer = Color.White,
  secondary = RunicGold,
  onSecondary = DeepAbyss,
  tertiary = CrimsonHealth,
  background = DeepAbyss,
  onBackground = LightText,
  surface = DarkSurface,
  onSurface = LightText,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = MutedText
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkFantasyColorScheme,
    typography = Typography,
    content = content
  )
}
