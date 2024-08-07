package io.github.leaguelugas.springmc.event

import org.bukkit.event.Event
import org.bukkit.event.Listener

interface SpringListener<out T : Event> : Listener {
    fun onEvent(event: @UnsafeVariance T)
}
