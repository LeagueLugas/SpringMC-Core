package io.github.leaguelugas.springmc.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryEventListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val gui = event.inventory.holder
        val clickedItem = event.currentItem
        val player = event.whoClicked as Player
        if (event.clickedInventory != null && gui is SGui && clickedItem != null) {
            event.isCancelled = true
            val consumer = gui.getClickLambda(slot = event.slot, itemStack = clickedItem)
            if (consumer == null) {
                gui.onClick(player, event)
            } else {
                consumer.accept(player, clickedItem)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val gui = event.inventory.holder
        val player = event.player as Player
        if (gui is SGui.Closeable) {
            gui.onInventoryClose(player, event)
        }
    }
}
