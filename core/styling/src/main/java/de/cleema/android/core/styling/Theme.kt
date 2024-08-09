/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.styling

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalCardElevation = staticCompositionLocalOf { Dp.Unspecified }

@Composable
fun CleemaTheme(
    useDarkTheme: Boolean = false, //isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val cardElevation = if (useDarkTheme) 4.dp else 0.dp

    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    CompositionLocalProvider(LocalCardElevation provides cardElevation) {
        MaterialTheme(
            colorScheme = colors,
            typography = CleemaTypography,
            shapes = shapes,
            content = content
        )
    }
}

val shapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(10.dp)
)

// see: https://m3.material.io/theme-builder#/custom

/*
   primary = Accent,
    onPrimary = Color.White,
    secondary = Action,
    onSecondary = Color.White,
    background = DefaultText
 */

private val LightColors = lightColorScheme(
    primary = DefaultText,
    onPrimary = Color.White,
//    primaryContainer = md_theme_light_primaryContainer,
//    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = Action,
    onSecondary = Color.White,
//    secondaryContainer = md_theme_light_secondaryContainer,
//    onSecondaryContainer = md_theme_light_onSecondaryContainer,
//    tertiary = md_theme_light_tertiary,
//    onTertiary = md_theme_light_onTertiary,
//    tertiaryContainer = md_theme_light_tertiaryContainer,
//    onTertiaryContainer = md_theme_light_onTertiaryContainer,
//    error = md_theme_light_error,
//    errorContainer = md_theme_light_errorContainer,
//    onError = md_theme_light_onError,
//    onErrorContainer = md_theme_light_onErrorContainer,
    background = Accent,
//    onBackground = md_theme_light_onBackground,
    surface = Color.White,
    onSurface = DefaultText,
//    surfaceVariant = md_theme_light_surfaceVariant,
//    onSurfaceVariant = md_theme_light_onSurfaceVariant,
//    outline = md_theme_light_outline,
//    inverseOnSurface = md_theme_light_inverseOnSurface,
//    inverseSurface = md_theme_light_inverseSurface,
//    inversePrimary = md_theme_light_inversePrimary,
//    surfaceTint = md_theme_light_surfaceTint,
//    outlineVariant = md_theme_light_outlineVariant,
//    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)
