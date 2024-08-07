package io.github.leaguelugas.springmc.di.resolvers

import io.github.leaguelugas.springmc.SpringMC
import io.github.leaguelugas.springmc.di.BeanResolver
import io.github.leaguelugas.springmc.di.annotations.EventListener
import io.github.leaguelugas.springmc.event.SpringListener
import org.bukkit.event.Event

class EventListenerResolver(
    private val plugin: SpringMC,
) : BeanResolver<EventListener> {
    override fun resolveBean(
        instance: Any,
        annotation: EventListener,
    ): Any {
        val listenerClass = "${instance.javaClass.`package`}.${instance.javaClass.simpleName}"
        if (instance !is SpringListener<*>) {
            throw IllegalArgumentException(
                "Event-Listener ($listenerClass) must implement SpringListener interface",
            )
        }

        val eventClass: Class<out Event> =
            instance.javaClass.declaredMethods
                .find { it.name == "onEvent" && it.parameters.isNotEmpty() }
                ?.parameters
                ?.let {
                    it.forEach { p ->
                        runCatching {
                            return@let p.type.asSubclass(Event::class.java)
                        }
                    }
                    return@let null
                }
                ?: throw IllegalArgumentException(
                    "Event-Listener ($listenerClass) must have a method named 'onEvent' with at least one parameter extending Event",
                )

        plugin.afterLoadingJob.add {
            plugin.server.pluginManager.registerEvent(eventClass, instance, annotation.priority, { listener, event ->
                if (listener is SpringListener<Event>) {
                    listener.onEvent(event)
                }
            }, plugin)
        }

        return instance
    }
}
