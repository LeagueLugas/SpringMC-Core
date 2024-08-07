package io.github.leaguelugas.springmc

import io.github.leaguelugas.springmc.di.DIContainer
import io.github.leaguelugas.springmc.gui.InventoryEventListener
import io.github.leaguelugas.springmc.util.Config
import org.bukkit.plugin.java.JavaPlugin

abstract class SpringMC : JavaPlugin() {
    private val applicationConfig = Config(this)
    private val diContainer =
        DIContainer(
            plugin = this,
            pluginFile = this.file,
        )
    val afterLoadingJob = mutableListOf<Runnable>()

    init {
        diContainer.scanBeans()
        afterLoadingJob.add { server.pluginManager.registerEvents(InventoryEventListener(), this) }
    }

    override fun onEnable() {
        afterLoadingJob.forEach(Runnable::run)
    }

    override fun reloadConfig() {
        super.reloadConfig()
        applicationConfig.reloadConfig()
    }
}
