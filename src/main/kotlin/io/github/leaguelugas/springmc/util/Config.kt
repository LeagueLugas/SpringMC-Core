package io.github.leaguelugas.springmc.util

import io.github.leaguelugas.springmc.SpringMC

class Config(
    private val plugin: SpringMC,
) {
    companion object {
        private lateinit var instance: Config

        fun getString(path: String): String? = instance.plugin.config.getString(path)

        fun <T> get(path: String): T = instance.plugin.config.get(path) as T

        fun reload() = instance.plugin.reloadConfig()
    }

    init {
        instance = this
        plugin.saveDefaultConfig()
    }
}
