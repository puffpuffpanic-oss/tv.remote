package com.example.tvremote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Placeholder. The real TV connection replaces this in Step 7.
object TvClient {
    var status by mutableStateOf("Not connected")

    fun sendKey(code: Int) {
        status = "Pressed key $code (TV not connected yet)"
    }

    fun sendText(text: String) {
        status = "Typed \"$text\" (TV not connected yet)"
    }
}
