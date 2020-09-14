package com.bunbeauty.ideal.myapplication.clean_architecture.domain.api

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

fun String.cutStringWithLineBreak(limit: Int): String {
    return if (this.split("\n").size > limit) {
        this.substring(0, this.split("\n")[0].length).trim { it <= ' ' } + " ..."
    } else {
        this
    }
}


