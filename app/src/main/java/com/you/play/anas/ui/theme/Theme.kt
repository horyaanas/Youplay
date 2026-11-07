package com.you.play.anas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = YouTubeRed,
  onPrimary = Color.White,
  primaryContainer = YouTubeRedDark,
  onPrimaryContainer = Color.White,
  background = YouTubeDarkBackground,
  onBackground = YouTubeDarkTextPrimary,
  surface = YouTubeDarkSurface,
  onSurface = YouTubeDarkTextPrimary,
  surfaceVariant = YouTubeDarkSurfaceVariant,
  onSurfaceVariant = YouTubeDarkTextSecondary,
  surfaceContainer = YouTubeDarkSurfaceElevated,
  outline = YouTubeDarkBorder,
  outlineVariant = YouTubeDarkChip,
  secondary = YouTubeBlue,
  onSecondary = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = YouTubeRed,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFFEBEB),
  onPrimaryContainer = YouTubeRed,
  background = YouTubeLightBackground,
  onBackground = YouTubeLightTextPrimary,
  surface = YouTubeLightSurface,
  onSurface = YouTubeLightTextPrimary,
  surfaceVariant = YouTubeLightSurfaceVariant,
  onSurfaceVariant = YouTubeLightTextSecondary,
  surfaceContainer = YouTubeLightSurfaceElevated,
  outline = YouTubeLightBorder,
  outlineVariant = YouTubeLightChip,
  secondary = Color(0xFF065FD4),
  onSecondary = Color.White
)

@Composable
fun YouPlayTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
