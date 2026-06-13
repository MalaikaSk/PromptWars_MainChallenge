package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SerenityDarkColorScheme =
  darkColorScheme(
    primary = CalmEmerald,
    secondary = CalmTeal,
    tertiary = WarmGold,
    background = DeepSlateBg,
    surface = CozySurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = CozySurface,
    secondaryContainer = DeepSlateBg
  )

private val SerenityLightColorScheme =
  lightColorScheme(
    primary = Color(0xFF00796B), // Deep Teal
    secondary = Color(0xFF0288D1), // Deep Blue
    tertiary = Color(0xFFF57C00), // Calm Amber
    background = Color(0xFFF1F5F9), // Light Slate
    surface = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force comforting dark mode by default for premium serenity feel
  dynamicColor: Boolean = false, // Use our gorgeous custom slate palette
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) SerenityDarkColorScheme else SerenityLightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
