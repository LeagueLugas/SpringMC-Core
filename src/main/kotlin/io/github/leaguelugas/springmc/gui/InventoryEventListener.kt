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
        if (event.clickedInventory != null && gui is GUI && clickedItem != null) {
            val consumer =
                if (gui is PageableGUI) {
                    gui.getClickLambda(page = gui.getCurrentPage(), slot = event.slot, itemStack = clickedItem)
                } else {
                    gui.getClickLambda(slot = event.slot, itemStack = clickedItem)
                }
            if (consumer == null) {
                gui.onClick(player, event)
            } else {
                event.isCancelled = true
                consumer.accept(player, clickedItem)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val gui = event.inventory.holder
        val player = event.player as Player
        if (gui is Closeable) {
            gui.onInventoryClose(player, event)
        }
    }
}
