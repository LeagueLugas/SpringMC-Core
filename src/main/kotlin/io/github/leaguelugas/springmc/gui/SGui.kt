package io.github.leaguelugas.springmc.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer

typealias InventoryHandler = BiConsumer<Player, ItemStack>

data class GuiItem(
    val slot: Int,
    val itemStack: ItemStack,
)

abstract class SGui(
    slotSize: Int,
    title: String,
) : InventoryHolder {
    private val inventory = Bukkit.createInventory(this, slotSize, title)
    private var clickMap: MutableMap<GuiItem, InventoryHandler?> = mutableMapOf()

    override fun getInventory(): Inventory = inventory

    fun addItem(
        slot: Int,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        inventory.setItem(slot, itemStack)
        val guiItem = GuiItem(slot, itemStack)
        clickMap[guiItem] = onClick
    }

    fun setTitle(title: String) {
        inventory.viewers.forEach { it.openInventory.title = title }
    }

    fun getClickLambda(
        slot: Int,
        itemStack: ItemStack,
    ): InventoryHandler? {
        val guiItem = GuiItem(slot, itemStack)
        return clickMap[guiItem]
    }

    fun open(player: Player) {
        player.openInventory(this.inventory)
    }

    abstract fun onClick(
        player: Player,
        event: InventoryClickEvent,
    )

    interface Closeable {
        fun onInventoryClose(
            player: Player,
            event: InventoryCloseEvent,
        )
    }
}
