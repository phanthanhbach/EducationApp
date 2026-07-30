package com.example.educationapp.core.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionManager {
    private val _sessionEvent = MutableSharedFlow<SessionEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sessionEvent: SharedFlow<SessionEvent> = _sessionEvent.asSharedFlow()

    fun notifySessionExpired() {
        _sessionEvent.tryEmit(SessionEvent.Expired)
    }
}

sealed interface SessionEvent {
    data object Expired : SessionEvent
}
