package io.github.leaguelugas.springmc.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer

typealias InventoryHandler = BiConsumer<Player, ItemStack>

data class GuiItem(
    val page: Int,
    val slot: Int,
    val itemStack: ItemStack,
)

abstract class GUI(
    slotSize: Int,
    title: String,
) : InventoryHolder {
    protected val initializedTitle = title
    private val inventory: Inventory = Bukkit.createInventory(this, slotSize, title)
    protected var clickMap: MutableMap<GuiItem, InventoryHandler?> = mutableMapOf()

    override fun getInventory(): Inventory = inventory

    abstract fun init()

    fun addItem(
        slot: Int,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        val guiItem = GuiItem(0, slot, itemStack)
        clickMap[guiItem] = onClick
    }

    fun addItem(
        slots: List<Int>,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        slots.forEach { slot ->
            addItem(slot, itemStack, onClick)
        }
    }

    fun setTitle(title: String) {
        inventory.viewers.forEach { it.openInventory.title = title }
    }

    open fun getClickLambda(
        slot: Int,
        itemStack: ItemStack,
    ): InventoryHandler? {
        val guiItem = GuiItem(0, slot, itemStack)
        return clickMap[guiItem]
    }

    fun open(player: Player) {
        player.openInventory(this.inventory)
    }

    fun renderGUI() {
        clearPage()
        clickMap
            .filter { it.key.page == 0 }
            .forEach { (item, _) ->
                inventory.setItem(item.slot, item.itemStack)
            }
    }

    protected fun clearPage() {
        inventory.clear()
    }

    abstract fun onClick(
        player: Player,
        event: InventoryClickEvent,
    )
}
