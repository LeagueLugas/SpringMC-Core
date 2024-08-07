package io.github.leaguelugas.springmc.util

import com.google.common.base.Charsets
import io.github.leaguelugas.springmc.SpringMC
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

class Config(
    private val plugin: SpringMC,
) {
    private companion object {
        const val CONFIG_NAME: String = "application.yml"
    }

    private val configFile = File(plugin.dataFolder, CONFIG_NAME)
    private var config: FileConfiguration = YamlConfiguration.loadConfiguration(configFile)

    init {
        if (!configFile.exists()) {
            plugin.saveResource(CONFIG_NAME, false)
        }
        reloadConfig()
    }

    fun reloadConfig() {
        plugin.getResource(CONFIG_NAME)?.let {
            YamlConfiguration.loadConfiguration(this.configFile).apply {
                setDefaults(
                    YamlConfiguration.loadConfiguration(
                        InputStreamReader(
                            it,
                            Charsets.UTF_8,
                        ),
                    ),
                )
            }
            plugin.logger.info("Reloaded config")
        }
    }

    fun getString(path: String): String? = this.config.getString(path)
}
