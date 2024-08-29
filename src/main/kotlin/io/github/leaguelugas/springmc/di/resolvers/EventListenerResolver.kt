package io.github.leaguelugas.springmc.di.resolvers

import io.github.leaguelugas.springmc.SpringMC
import io.github.leaguelugas.springmc.di.BeanResolver
import io.github.leaguelugas.springmc.di.annotations.EventListener
import io.github.leaguelugas.springmc.event.SpringListener

class EventListenerResolver(
    private val plugin: SpringMC,
) : BeanResolver<EventListener> {
    override fun resolveBean(
        instance: Any,
        annotation: EventListener,
    ): Any {
        val listenerClass = "${instance.javaClass.`package`}.${instance.javaClass.simpleName}"
        if (instance !is SpringListener) {
            throw IllegalArgumentException(
                "Event-Listener ($listenerClass) must implement SpringListener interface",
            )
        }

        plugin.afterLoadingJob.add {
            plugin.server.pluginManager.registerEvents(instance, plugin)
        }

        return instance
    }
}
