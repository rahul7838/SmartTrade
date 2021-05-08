package com.example.smarttrade.extension

private const val SLASH_PATTERN = "/"
private const val HYPHEN_PATTERN = "-"


fun String.replaceSlash(): String {
    return this.replace(SLASH_PATTERN, HYPHEN_PATTERN)
}