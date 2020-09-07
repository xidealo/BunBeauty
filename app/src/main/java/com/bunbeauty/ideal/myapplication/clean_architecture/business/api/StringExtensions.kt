package com.bunbeauty.ideal.myapplication.clean_architecture.business.api

fun String.firstCapitalSymbol(): String {
    return if (this.length > 1) {
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    } else {
        this
    }
}

fun String.cutStringWithDots(limit: Int): String {
    return if (this.length > limit) {
        this.substring(0, limit).trim { it <= ' ' } + "..."
    } else {
        this
    }
}


