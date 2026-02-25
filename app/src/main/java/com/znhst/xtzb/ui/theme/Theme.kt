package com.znhst.xtzb.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Primary50,
    onPrimary = Color.White,
    primaryContainer = Primary80,
    onPrimaryContainer = Primary10,
    secondary = Neutral60,
    onSecondary = Color.White,
    secondaryContainer = Neutral80,
    onSecondaryContainer = Neutral20,
    tertiary = Primary30,
    background = Color(0xFF0F1419),
    onBackground = Neutral20,
    surface = Color(0xFF1A1F2E),
    onSurface = Neutral20,
    surfaceVariant = Color(0xFF252D3A),
    onSurfaceVariant = Neutral40,
    outline = Neutral60,
    outlineVariant = Neutral70
)

private val LightColorScheme = lightColorScheme(
    primary = Primary60,
    onPrimary = Color.White,
    primaryContainer = Primary10,
    onPrimaryContainer = Primary90,
    secondary = Neutral60,
    onSecondary = Color.White,
    secondaryContainer = Neutral20,
    onSecondaryContainer = Neutral80,
    tertiary = Primary30,
    background = Neutral10,
    onBackground = Neutral100,
    surface = Color.White,
    onSurface = Neutral100,
    surfaceVariant = Neutral20,
    onSurfaceVariant = Neutral60,
    outline = Neutral50,
    outlineVariant = Neutral30,
    error = Error50,
    errorContainer = Error10
)

@Composable
fun ZB_CanteenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
