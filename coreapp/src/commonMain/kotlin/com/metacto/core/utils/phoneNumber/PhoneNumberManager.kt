package com.metacto.core.utils.phoneNumber

import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil
import io.michaelrocks.libphonenumber.kotlin.Phonenumber

internal class PhoneNumberManager(
    private val phoneNumberUtil: PhoneNumberUtil
) : IPhoneNumberManager {

    override fun getValidPhoneNumber(
        number: String,
        countryCode: String?
    ): String? {
        val regex = Regex("[^+0-9]")

        // Remove all non-numeric characters except +
        val updatedNumber = regex.replace(number, "").replace(" ", "")
        // create phone number util instance

        // parse the updated number without country code and check if it's valid
        val internationalPhoneNumber = parsePhoneNumber(updatedNumber, countryCode)
        // if valid, format it to international format
        if (internationalPhoneNumber != null) {
            // return the formatted number
            return phoneNumberUtil.format(
                internationalPhoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            )
        } else {
            // if not valid, try to parse it with US country code
            val usPhoneNumber = parsePhoneNumber(updatedNumber, countryCode)
            // if valid, format it to international format
            if (usPhoneNumber != null) {
                // return the formatted number
                return phoneNumberUtil.format(
                    usPhoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
                )
            }
        }

        // if not valid, return null
        return null
    }

    override fun getFormattedPhoneNumber(number: String, countryCode: String?): String? {
        val phoneNumber = parsePhoneNumber(number, countryCode) ?: return null
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }

    override fun getE164FormattedPhoneNumber(number: String, countryCode: String?): String? {
        val phoneNumber = parsePhoneNumber(number, countryCode) ?: return null
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
    }

    override fun isValidPhoneNumber(number: String, countryCode: String?): Boolean {
        val phoneNumber = parsePhoneNumber(number, countryCode) ?: return false
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    private fun parsePhoneNumber(
        number: String,
        countryCode: String?
    ): Phonenumber.PhoneNumber? {
        try {
            val phoneNumber = phoneNumberUtil.parse(number, countryCode)
            val phoneNumberType = phoneNumberUtil.getNumberType(phoneNumber)
            val isValidPhoneType =
                phoneNumberType == PhoneNumberUtil.PhoneNumberType.MOBILE || phoneNumberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE
            return if (isValidPhoneType) {
                phoneNumber
            } else {
                null
            }
        } catch (_: Exception) {
        }
        return null
    }
}