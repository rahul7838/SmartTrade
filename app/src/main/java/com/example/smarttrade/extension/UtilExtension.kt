package com.example.smarttrade.extension

import android.content.Context
import android.widget.Toast

fun String.toDouble(message: String, context: Context): Double {
    return if(this.isBlank()) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        0.0
    } else {
        this.toDouble()
    }
}