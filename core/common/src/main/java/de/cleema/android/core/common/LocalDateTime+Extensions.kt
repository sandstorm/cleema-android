package de.cleema.android.core.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun LocalDateTime.formatted(style: FormatStyle): String =
    DateTimeFormatter.ofLocalizedDate(style).format(this.toJavaLocalDateTime())

fun LocalDate.formatted(style: FormatStyle): String =
    DateTimeFormatter.ofLocalizedDate(style).format(this.toJavaLocalDate())

