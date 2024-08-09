/*
 * Created by Kumpels and Friends on 2022-12-05
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.styling

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Montserrat = FontFamily(
    Font(R.font.montserrat_regular),
    Font(R.font.montserrat_semibold, FontWeight.W600)
)

val CleemaTypography = Typography(
    headlineMedium = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp
    ),
    headlineSmall = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    displaySmall = TextStyle(
        color = DefaultText,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)
