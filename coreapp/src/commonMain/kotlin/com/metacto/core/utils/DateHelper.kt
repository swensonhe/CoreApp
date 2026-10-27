package com.metacto.core.utils

import com.metacto.core.domain.CoreConstants
import com.metacto.strapikmm.util.DatetimeUtil
import com.metacto.strapikmm.util.minusDays
import com.metacto.strapikmm.util.plusDays
import com.metacto.strapikmm.util.toLocalDate
import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Deprecated("Use com.metacto.core.utils.date.DateTimeUtils instead", ReplaceWith("DateTimeUtils"))
expect object DateHelper {
    fun format(instant: Instant, format: String): String

    @Throws(Throwable::class)
    fun stringToDate(string: String, format: String): Date

    @Throws(Throwable::class)
    fun utcStringToDate(string: String, format: String): Date

    @Throws(Throwable::class)
    fun dateToString(date: Date, format: String): String

    @Throws(Throwable::class)
    fun convertDateFormat(date: String, fromFormat: String, toFormat: String): String

    @Throws(Throwable::class)
    fun dateToUTCString(date: Date, format: String): String

    @Throws(Throwable::class)
    fun daysInMonth(year: Int, month: Int): Int

    @Throws(Throwable::class)
    fun timestampToReadableDate(timestamp: Long): String

    @Throws(Throwable::class)
    fun getDayOfMonthSuffix(date: Date): String

    @Throws(Throwable::class)
    fun getElapsedYears(date: Date): Int

    @Throws(Throwable::class)
    fun getCurrentLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate

    @Throws(Throwable::class)
    fun getMondayOfWeek(date: LocalDate): LocalDate

    @Throws(Throwable::class)
    fun getSundayOfWeek(date: LocalDate): LocalDate
}

expect class Date()

expect fun Date.toMillis(): Long

expect fun dateFromTimestamp(timestamp: Long): Date

fun getCurrentDate(): LocalDate {
    return DatetimeUtil.now().toLocalDate()
}

fun getCurrentTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    return Clock.System.now().toLocalDateTime(timeZone).time
}

fun LocalTime.get12FormatHour(): Int {
    val validHour = hour.mod(12)
    return if (validHour == 0) 12 else validHour
}

fun LocalTime.isAM(): Boolean {
    return hour in 0 until 12
}

fun LocalTime.isPM(): Boolean {
    return hour in 12 until 24
}

fun LocalTime.to12FormatString(): String {
    val hour = (hour % 12).toString().padStart(2, '0')
    val period = if (this.hour < 12) "AM" else "PM"
    val minute = minute.toString().padStart(2, '0')
    return "$hour:$minute $period"
}

fun LocalDate.mergeWith(localTime: LocalTime): LocalDateTime {
    return atTime(localTime)
}

fun LocalDateTime.toInstant(): Instant {
    return this.toInstant(TimeZone.currentSystemDefault())
}

fun LocalDate.toLocalDateTime(): LocalDateTime {
    return LocalDateTime(
        date = this,
        time = LocalTime(0, 0)
    )
}

fun LocalDate.toInstant(): Instant {
    return this.toLocalDateTime().toInstant()
}

fun LocalDate.toEpochMilliseconds(): Long {
    return this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

fun Long.toLocalDate(): LocalDate {
    return this.toLocalDate().toLocalDate()
}

fun nowInstant(): Instant {
    return DatetimeUtil.now().toInstant(TimeZone.currentSystemDefault())
}

fun LocalDate.toFormattedDate(format: String): String {
    return DateHelper.format(
        instant = this.toInstant(),
        format = format
    )
}

fun LocalDate.toServerDate(): String {
    return this.toFormattedDate(CoreConstants.SERVER_DATE_FORMAT)
}


fun String.parseServerDate(): LocalDate? {
    return try {
        DateHelper.stringToDate(
            string = this,
            format = CoreConstants.SERVER_DATE_FORMAT
        ).toMillis().toLocalDate().date
    } catch (_: Throwable) {
        null
    }
}

fun String.parseDate(format: String): LocalDate? {
    return try {
        DateHelper.stringToDate(
            string = this,
            format = format
        ).toMillis().toLocalDate().date
    } catch (_: Throwable) {
        null
    }
}

fun Clock.getCurrentLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate {
    return this.now().toLocalDateTime(timeZone).date
}

fun LocalDate.getDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
    val currentDayOfWeek = this.dayOfWeek
    return this.plusDays(dayOfWeek.isoDayNumber - currentDayOfWeek.isoDayNumber)
}

fun getCurrentWeekDates(timeZone: TimeZone = TimeZone.currentSystemDefault()): List<LocalDate> {
    val currentDate = Clock.System.now().toLocalDateTime(timeZone).date
    val startOfWeek = currentDate.minusDays(currentDate.dayOfWeek.isoDayNumber % 7)
    return (0..6).map { startOfWeek.plusDays(it) }
}