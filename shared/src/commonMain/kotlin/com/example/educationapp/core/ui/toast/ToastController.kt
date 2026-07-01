package com.example.educationapp.core.ui.toast

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_launcher

@Stable
class ToastController(private val coroutineScope: CoroutineScope) {
    var message by mutableStateOf("")
        private set
    var visible by mutableStateOf(false)
        private set
    var logoIcon by mutableStateOf<DrawableResource>(Res.drawable.ic_launcher)
        private set

    private var dismissJob: Job? = null

    fun show(msg: String, icon: DrawableResource = Res.drawable.ic_launcher, durationMs: Long = 2000) {
        message = msg
        logoIcon = icon
        visible = true
        
        dismissJob?.cancel()
        dismissJob = coroutineScope.launch {
            delay(durationMs)
            visible = false
        }
    }
    
    fun dismiss() {
        visible = false
        dismissJob?.cancel()
    }
}

val LocalToastController = staticCompositionLocalOf<ToastController> {
    error("No ToastController provided")
}
