package com.bunbeauty.ideal.myapplication.clean_architecture.domain.api

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * round symbol
 *
 * @param number will round
 *
 * @param symbolsCount how many symbols round
 *
 * @return new rounded symbol
 */
fun Number.roundSomeSymbols(symbolsCount: Int): Float {
    return BigDecimal(this.toDouble()).setScale(symbolsCount, RoundingMode.HALF_EVEN)
        .toFloat()
}
