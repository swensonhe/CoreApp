package com.metacto.core.presentation.components.wheelPicker.datetime

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import com.metacto.core.presentation.components.wheelPicker.SelectorProperties
import com.metacto.core.presentation.components.wheelPicker.WheelPickerDefaults
import com.metacto.core.presentation.theme.CoreTheme
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun WheelDateTimePicker(
    modifier: Modifier = Modifier,
    startDateTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    minDateTime: LocalDateTime = LocalDateTime.EPOCH,
    maxDateTime: LocalDateTime = LocalDateTime.CYBER_ERA,
    yearsRange: IntRange? = IntRange(minDateTime.year, maxDateTime.year),
    timeFormat: TimeFormat = TimeFormat.HOUR_24,
    size: DpSize = DpSize(
        width = CoreTheme.spacings.wheelDateTimePicker.pickerWidth,
        height = CoreTheme.spacings.wheelDateTimePicker.pickerHeight
    ),
    rowCount: Int = 3, // Number of rows to show
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDateTime: (snappedDateTime: LocalDateTime) -> Unit = {}
) {
    DefaultWheelDateTimePicker(
        modifier = modifier,
        startDateTime = startDateTime,
        minDateTime = minDateTime,
        maxDateTime = maxDateTime,
        yearsRange = yearsRange,
        timeFormat = timeFormat,
        size = size,
        rowCount = rowCount,
        textStyle = textStyle,
        textColor = textColor,
        selectorProperties = selectorProperties,
        onSnappedDateTime = { snappedDateTime ->
            onSnappedDateTime(snappedDateTime.snappedLocalDateTime)
            snappedDateTime.snappedIndex
        }
    )
}