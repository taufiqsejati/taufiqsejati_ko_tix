package com.taufiqsejati.kotix.utils

import android.content.Intent
import android.os.Build
import java.io.Serializable

// Langsung tulis fungsinya di luar kelas (Top-level function)
inline fun <reified T : Serializable> Intent.getSerializableData(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
}
