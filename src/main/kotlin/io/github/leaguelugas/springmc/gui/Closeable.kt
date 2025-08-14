package io.github.leaguelugas.springmc.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent

interface Closeable {
    fun onInventoryClose(
        player: Player,
        event: InventoryCloseEvent,
    )
}
