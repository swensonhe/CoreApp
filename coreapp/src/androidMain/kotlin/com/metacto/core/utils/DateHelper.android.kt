package com.metacto.core.utils

import android.icu.text.SimpleDateFormat
import android.text.format.DateUtils
import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone as JavaTimeZone

actual typealias Date = java.util.Date

actual fun Date.toMillis(): Long = this.time

actual fun dateFromTimestamp(timestamp: Long): Date {
    return Date(timestamp)
}

@Deprecated("Use com.metacto.core.utils.date.DateTimeUtils instead", ReplaceWith("DateTimeUtils"))
actual object DateHelper {
    actual fun format(instant: Instant, format: String): String {
        val timestamp = instant.toEpochMilliseconds()
        val date = Date(timestamp)

        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    @Throws(Throwable::class)
    actual fun stringToDate(
        string: String,
        format: String
    ): Date {
        val sdf = java.text.SimpleDateFormat(format, Locale.getDefault())
        return sdf.parse(string) ?: throw Throwable("Can't parse $string")
    }

    @Throws(Throwable::class)
    actual fun utcStringToDate(
        string: String,
        format: String
    ): Date {
        val sdf = java.text.SimpleDateFormat(format, Locale.getDefault()).apply {
            timeZone = JavaTimeZone.getTimeZone("UTC")
        }

        return sdf.parse(string) ?: throw Throwable("Can't convert $string using format ($format)")
    }

    @Throws(Throwable::class)
    actual fun dateToString(
        date: Date,
        format: String
    ): String {
        val sdf = java.text.SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }

    @Throws(Throwable::class)
    actual fun convertDateFormat(
        date: String,
        fromFormat: String,
        toFormat: String
    ): String {
        val parsedDate = stringToDate(string = date, format = fromFormat)
        return dateToString(date = parsedDate, format = toFormat)
    }

    @Throws(Throwable::class)
    actual fun dateToUTCString(date: Date, format: String): String {
        val sdf = java.text.SimpleDateFormat(format, Locale.getDefault()).apply {
            timeZone = JavaTimeZone.getTimeZone("UTC")
        }

        return sdf.format(date)
    }

    @Throws(Throwable::class)
    actual fun daysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance().apply {
            // Reset the calendar day to the first day of the month to avoid rolling over to the next month
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }

        return calendar.getActualMaximum(Calendar.DATE)
    }

    @Throws(Throwable::class)
    actual fun timestampToReadableDate(timestamp: Long): String {
        val now = java.util.Date().time
        val difference = now - timestamp

        return when {
            difference < DateUtils.MINUTE_IN_MILLIS -> "Just now"
            else -> DateUtils
                .getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS)
                .toString()
        }
    }

    @Throws(Throwable::class)
    actual fun getDayOfMonthSuffix(date: Date): String {
        return when (dateToString(date, "d").toIntOrNull()) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    @Throws(Throwable::class)
    actual fun getElapsedYears(date: Date): Int {
        val millisInYear = 31536000000L
        val millisDiff = Date().toMillis() - date.toMillis()
        return (millisDiff.toDouble() / millisInYear.toDouble()).toInt()
    }

    @Throws(Throwable::class)
    actual fun getCurrentLocalDate(timeZone: TimeZone): LocalDate {
        return Clock.System.getCurrentLocalDate(timeZone)
    }

    @Throws(Throwable::class)
    actual fun getMondayOfWeek(date: LocalDate): LocalDate {
        return date.getDayOfWeek(DayOfWeek.MONDAY)
    }

    @Throws(Throwable::class)
    actual fun getSundayOfWeek(date: LocalDate): LocalDate {
        return date.getDayOfWeek(DayOfWeek.SUNDAY)
    }
}