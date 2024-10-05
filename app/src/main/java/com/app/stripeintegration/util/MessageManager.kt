package com.app.stripeintegration.util

class MessageManager {

    fun <T: Any> getByResult(result: T): String {
        val message = result::class.simpleName ?: "Unknown"
        return "Result: $message"
    }

}