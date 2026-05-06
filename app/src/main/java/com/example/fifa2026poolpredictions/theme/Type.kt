package com.example.fifa2026poolpredictions.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,     fontSize = 24.sp),
    titleLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold,     fontSize = 22.sp),
    titleMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    titleSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyLarge      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodySmall      = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
    labelLarge     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 14.sp),
    labelMedium    = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 12.sp),
    labelSmall     = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 10.sp),
)

// Monospace score styles — not Material3 roles, app-specific
val ScoreTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize   = 16.sp,
)
val ScoreBadgeTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Black,
    fontSize   = 16.sp,
)
