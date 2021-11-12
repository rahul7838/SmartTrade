package com.example.smarttrade.extension

import com.fasterxml.jackson.databind.ObjectMapper

fun Any.toJsonString(): String {
    val objectMapper = ObjectMapper()
    return objectMapper.writeValueAsString(this)
}

inline fun <reified T> String.toObject(): T {
    return ObjectMapper().readValue(this, T::class.java)
}