package io.github.leaguelugas.springmc.di.resolvers

import io.github.leaguelugas.springmc.SpringMC
import io.github.leaguelugas.springmc.di.DIContainer
import io.github.leaguelugas.springmc.di.annotations.Scheduled
import org.bukkit.scheduler.BukkitRunnable

class ScheduledResolver(private val container: DIContainer) {

    private val plugin = container.get<SpringMC>()

    fun resolve() {
        val components = container.getComponents()
        for (component in components) {
            component::class.java.methods.forEach { method ->
                method.getAnnotation(Scheduled::class.java)?.let { scheduled ->
                    val runnable = object : BukkitRunnable() {
                        override fun run() {
                            method.invoke(component)
                        }
                    }

                    if (scheduled.period > 0) {
                        if (scheduled.async) {
                            runnable.runTaskTimerAsynchronously(plugin, scheduled.delay, scheduled.period)
                        } else {
                            runnable.runTaskTimer(plugin, scheduled.delay, scheduled.period)
                        }
                    } else {
                        if (scheduled.async) {
                            runnable.runTaskLaterAsynchronously(plugin, scheduled.delay)
                        } else {
                            runnable.runTaskLater(plugin, scheduled.delay)
                        }
                    }
                }
            }
        }
    }
}
