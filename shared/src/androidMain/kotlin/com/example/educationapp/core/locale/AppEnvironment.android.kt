package com.example.educationapp.core.locale

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLocale
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = LocalLocale.current.platformLocale.toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        if (default == null) {
            default = LocalLocale.current.platformLocale
        }
        val new = when (value) {
            null -> default!!
            else -> Locale.forLanguageTag(value)
        }
        Locale.setDefault(new)
        val newConfiguration = Configuration(configuration).apply {
            setLocale(new)
        }
        return LocalConfiguration.provides(newConfiguration)
    }
}
