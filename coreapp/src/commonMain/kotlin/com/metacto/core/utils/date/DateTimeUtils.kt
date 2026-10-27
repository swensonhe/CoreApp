package com.metacto.core.utils.date

import com.metacto.core.utils.extensions.getSystemLanguage
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun Long.toLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return this.toLocalDateTime(timeZone).date
}

fun Long.toLocalTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    return this.toLocalDateTime(timeZone).time
}

fun LocalDate.toMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.atStartOfDayIn(timeZone).toEpochMilliseconds()
}

fun LocalDateTime.toMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone).toEpochMilliseconds()
}

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone)
}

fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return LocalDateTime.now(timeZone).date
}

fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    return LocalDateTime.now(timeZone).time
}

fun LocalDate.isPast(): Boolean {
    return this < LocalDate.now()
}

expect fun LocalDateTime.format(
    format: String,
    langCode: String = getSystemLanguage().code
): String

expect fun LocalDate.format(
    format: String,
    langCode: String = getSystemLanguage().code
): String

expect fun LocalTime.format(
    format: String,
    langCode: String = getSystemLanguage().code
): String

expect fun String.parseLocalDateTime(
    format: String,
    langCode: String = getSystemLanguage().code
): LocalDateTime?

expect fun String.parseLocalDate(
    format: String,
    langCode: String = getSystemLanguage().code
): LocalDate?

expect fun String.parseLocalTime(
    format: String,
    langCode: String = getSystemLanguage().code
): LocalTime?

expect fun LocalDate.formatToRelativeDate(): String

expect fun LocalDateTime.formatToRelativeDate(): String