package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SpotifyDarkColorScheme = darkColorScheme(
  primary = ThemePrimary,
  secondary = ThemeInactiveText,
  background = ThemeBackground,
  surface = ThemeSurface,
  onPrimary = ThemeOnPrimary,
  onBackground = ThemeOnBackground,
  onSurface = ThemeOnSurface,
  surfaceVariant = ThemeBorder,
  onSurfaceVariant = ThemeOnSurface
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default for authentic Spotify experience
  dynamicColor: Boolean = false, // Disable dynamic colors to keep Spotify's brand identity
  content: @Composable () -> Unit,
) {
  val colorScheme = SpotifyDarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
