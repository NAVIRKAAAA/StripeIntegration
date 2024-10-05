package com.app.stripeintegration.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun apiHandler(
    block: suspend () -> Unit,
) {
    withContext(Dispatchers.IO) {
        try {
            block()
        } catch (e: Exception) {
            return@withContext
        }
    }
}