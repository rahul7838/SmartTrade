package com.example.smarttrade.extension

import java.lang.StringBuilder

private const val SLASH_PATTERN = "/"
private const val HYPHEN_PATTERN = "-"


fun String.replaceSlash(): String {
    return this.replace(SLASH_PATTERN, HYPHEN_PATTERN)
}

fun String.appendCurrency(): String {
    return StringBuilder(this).insert(0, "Rs.").toString()
}