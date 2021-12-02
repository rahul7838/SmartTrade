package com.example.smarttrade.mock

import com.zerodhatech.models.Order

fun getMockOrder(): Order {
    return Order().apply {
        orderId = "12345"
    }
}