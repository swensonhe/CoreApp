package com.metacto.core.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.metacto.core.domain.CoreConstants.US_COUNTRY_CODE
import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil

internal class PhoneNumberVisualTransformation(
    phoneNumberUtil: PhoneNumberUtil,
    countryCode: String = US_COUNTRY_CODE
) : VisualTransformation {

    private val phoneNumberFormatter = phoneNumberUtil.getAsYouTypeFormatter(countryCode)

    override fun filter(text: AnnotatedString): TransformedText {
        val transformation = reformat(text, text.length)

        return TransformedText(
            AnnotatedString(transformation.formatted.orEmpty()),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    if (transformation.formatted.isNullOrEmpty()) return 0
                    return transformation.originalToTransformed
                        .getOrElse(offset) { transformation.formatted.length }
                        .coerceIn(0, transformation.formatted.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (text.isEmpty()) return 0
                    return transformation.transformedToOriginal
                        .getOrElse(offset) { text.length }
                        .coerceIn(0, text.length)
                }
            }
        )
    }

    private fun isNonSeparator(c: Char): Boolean {
        return (c in '0'..'9') || c == '*' || c == '#' || c == '+' || c == 'N' || c == ';' || c == ','
    }

    private fun reformat(s: CharSequence, cursor: Int): Transformation {
        phoneNumberFormatter.clear()

        val curIndex = cursor - 1
        var formatted: String? = null
        var lastNonSeparator = 0.toChar()
        var hasCursor = false

        s.forEachIndexed { index, char ->
            if (isNonSeparator(char)) {
                if (lastNonSeparator.code != 0) {
                    formatted = getFormattedNumber(lastNonSeparator, hasCursor)
                    hasCursor = false
                }
                lastNonSeparator = char
            }
            if (index == curIndex) {
                hasCursor = true
            }
        }

        if (lastNonSeparator.code != 0) {
            formatted = getFormattedNumber(lastNonSeparator, hasCursor)
        }

        val originalToTransformed = mutableListOf<Int>()
        val transformedToOriginal = mutableListOf<Int>()
        var specialCharsCount = 0

        formatted?.forEachIndexed { index, char ->
            if (!isNonSeparator(char)) {
                specialCharsCount++
            } else {
                originalToTransformed.add(index)
                transformedToOriginal.add(index - specialCharsCount)
            }
        }

        // Ensure there's always at least one mapping
        if (originalToTransformed.isEmpty()) originalToTransformed.add(0)
        if (transformedToOriginal.isEmpty()) transformedToOriginal.add(0)

        return Transformation(formatted, originalToTransformed, transformedToOriginal)
    }

    private fun getFormattedNumber(lastNonSeparator: Char, hasCursor: Boolean): String? {
        return if (hasCursor) {
            phoneNumberFormatter.inputDigitAndRememberPosition(lastNonSeparator)
        } else {
            phoneNumberFormatter.inputDigit(lastNonSeparator)
        }
    }

    private data class Transformation(
        val formatted: String?,
        val originalToTransformed: List<Int>,
        val transformedToOriginal: List<Int>
    )
}