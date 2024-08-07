package io.github.leaguelugas.springmc.di.annotations

import org.bukkit.event.EventPriority

@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventListener(
    val priority: EventPriority = EventPriority.NORMAL,
)
