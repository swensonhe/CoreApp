package com.metacto.core.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierISO8601
import platform.Foundation.NSCalendarOptions
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSCalendarUnitWeekOfMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateComponentsFormatter
import platform.Foundation.NSDateComponentsFormatterUnitsStyleAbbreviated
import platform.Foundation.NSDateComponentsFormatterZeroFormattingBehaviorDropAll
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.localTimeZone
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeZoneWithAbbreviation

actual typealias Date = NSDate

actual fun Date.toMillis(): Long {
    return (timeIntervalSince1970 * 1000).toLong()
}

actual fun dateFromTimestamp(timestamp: Long): Date {
    return NSDate.dateWithTimeIntervalSince1970(timestamp.div(1000).toDouble())
}

fun NSDate.toDateComponents(): NSDateComponents {
    // Create a calendar instance
    val calendar = NSCalendar.currentCalendar

    // Extract date components from the NSDate
    return calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond,
        this
    )
}

@Deprecated("Use com.metacto.core.utils.date.DateTimeUtils instead", ReplaceWith("DateTimeUtils"))
actual object DateHelper {
    private val timeFormatter = NSDateComponentsFormatter().apply {
        unitsStyle = NSDateComponentsFormatterUnitsStyleAbbreviated
        zeroFormattingBehavior = NSDateComponentsFormatterZeroFormattingBehaviorDropAll
        maximumUnitCount = 1
        allowedUnits = (
                NSCalendarUnitYear
                        or NSCalendarUnitMonth
                        or NSCalendarUnitWeekOfMonth
                        or NSCalendarUnitDay
                        or NSCalendarUnitHour
                        or NSCalendarUnitMinute
                )
        includesApproximationPhrase = false
    }

    private val dateFormatter = NSDateFormatter().apply {
        locale = NSLocale.currentLocale
        lenient = true
        calendar = NSCalendar(calendarIdentifier = NSCalendarIdentifierISO8601)
    }

    private val utcTimeZone: NSTimeZone
        get() = NSTimeZone.timeZoneWithAbbreviation("UTC")
            ?: throw Throwable("Unable to construct UTC TimeZone")

    private val localTimeZone: NSTimeZone
        get() = NSTimeZone.localTimeZone

    actual fun format(instant: Instant, format: String): String {
        return formatInstantToiOSDateTime(instant.toEpochMilliseconds(), format)
    }

    private fun formatInstantToiOSDateTime(instant: Long, format: String): String {
        val timestamp = instant / 1000.0 // Convert from milliseconds to seconds
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp)

        val dateFormatter = NSDateFormatter().apply {
            setLocale(NSLocale(localeIdentifier = "en_US"))
            setDateFormat(format)
        }

        return dateFormatter.stringFromDate(date)
    }

    @Throws(Throwable::class)
    actual fun stringToDate(
        string: String,
        format: String
    ): Date {
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = localTimeZone
        return dateFormatter.dateFromString(string) ?: NSDate()
    }

    @Throws(Throwable::class)
    actual fun dateToString(
        date: Date,
        format: String
    ): String {
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = localTimeZone
        return dateFormatter.stringFromDate(date)
    }

    @Throws(Throwable::class)
    actual fun convertDateFormat(
        date: String,
        fromFormat: String,
        toFormat: String
    ): String {
        val formattedDate = stringToDate(date, fromFormat)
        return dateToString(formattedDate, toFormat)
    }

    @Throws(Throwable::class)
    actual fun dateToUTCString(
        date: Date,
        format: String
    ): String {
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = utcTimeZone
        return dateFormatter.stringFromDate(date)
    }

    @Throws(Throwable::class)
    actual fun utcStringToDate(
        string: String,
        format: String
    ): Date {
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = utcTimeZone

        val date = dateFormatter.dateFromString(string)
            ?: throw Throwable("Unable to convert $string to date with format $format")
        val dateString = dateToString(date, format)

        dateFormatter.timeZone = localTimeZone
        return dateFormatter.dateFromString(dateString)
            ?: throw Throwable("Unable to convert $dateString to date with $format")
    }

    @OptIn(ExperimentalForeignApi::class)
    @Throws(Throwable::class)
    actual fun daysInMonth(year: Int, month: Int): Int {
        val calendar = NSCalendar.currentCalendar
        val components = NSDateComponents().apply {
            // Reset the calendar day to the first day of the month to avoid rolling over to the next month
            this.day = 1.convert()
            this.month = month.convert()
            this.year = year.convert()
        }

        val date = calendar.dateFromComponents(components) ?: return 0
        val range = calendar.rangeOfUnit(NSCalendarUnitDay, NSCalendarUnitMonth, date)
        return range.size
    }

    @Throws(Throwable::class)
    actual fun timestampToReadableDate(timestamp: Long): String {
        // Prepare timestamp seconds
        val timestampSeconds = timestamp.div(1000).toDouble()

        // Create dates
        val now = NSDate()
        val date = dateFromTimestamp(timestamp)

        // Check and return
        return when {
            (now.timeIntervalSince1970 - timestampSeconds) <= 59 -> {
                // Less than one minute
                "Just Now"
            }

            else -> {
                // Or convert to readable date
                timeFormatter.stringFromDate(startDate = date, toDate = now).orEmpty()
            }
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
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitYear,
            fromDate = date,
            toDate = NSDate(),
            options = NSCalendarOptions.MAX_VALUE
        )

        return components.year.toInt()
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