package com.bunbeauty.ideal.myapplication.clean_architecture.business.api

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * round symbols
 */
class NumberRoundingApi {

    /**
     * round symbol
     *
     * @param number will round
     *
     * @param symbolsCount how many symbols round
     *
     * @return new rounded symbol
     */
    fun <T : Number> roundSomeSymbols(number: T, symbolsCount: Int): Float {
        return BigDecimal(number.toDouble()).setScale(symbolsCount, RoundingMode.HALF_EVEN)
            .toFloat()
    }
}