package com.msa.studyassistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * لوحة الألوان: أساس تركوازي هادئ مناسب للطلاب، مع دعم كامل للوضع الداكن.
 */

private val TealLight = Color(0xFF00696E)
private val TealDark = Color(0xFF4DDADF)

val LightColors = androidx.compose.material3.lightColorScheme(
    primary = TealLight,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9CF1F6),
    onPrimaryContainer = Color(0xFF002021),
    secondary = Color(0xFF4A6365),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8E9),
    onSecondaryContainer = Color(0xFF051F21),
    tertiary = Color(0xFF4B607C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E4FF),
    onTertiaryContainer = Color(0xFF041C35),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF9FBFB),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFF9FBFB),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE4E5),
    onSurfaceVariant = Color(0xFF3F4949),
    outline = Color(0xFF6F7979),
    outlineVariant = Color(0xFFBEC8C9),
)

val DarkColors = androidx.compose.material3.darkColorScheme(
    primary = TealDark,
    onPrimary = Color(0xFF003739),
    primaryContainer = Color(0xFF004F52),
    onPrimaryContainer = Color(0xFF9CF1F6),
    secondary = Color(0xFFB0CBCC),
    onSecondary = Color(0xFF1B3536),
    secondaryContainer = Color(0xFF324B4C),
    onSecondaryContainer = Color(0xFFCCE8E9),
    tertiary = Color(0xFFB3C8E8),
    onTertiary = Color(0xFF1C314B),
    tertiaryContainer = Color(0xFF334863),
    onTertiaryContainer = Color(0xFFD3E4FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0E1414),
    onBackground = Color(0xFFDEE3E3),
    surface = Color(0xFF0E1414),
    onSurface = Color(0xFFDEE3E3),
    surfaceVariant = Color(0xFF3F4949),
    onSurfaceVariant = Color(0xFFBEC8C9),
    outline = Color(0xFF889392),
    outlineVariant = Color(0xFF3F4949),
)

/** ألوان مميزة لكل مادة (نقطة ملونة بجانب اسم المادة). */
private val SubjectDotColors = mapOf(
    "math" to Color(0xFF4C5FD7),
    "english" to Color(0xFF2E9E6B),
    "chemistry" to Color(0xFFC75B39),
)

@Composable
fun subjectDotColor(subjectId: String): Color =
    SubjectDotColors[subjectId] ?: MaterialTheme.colorScheme.outline
