package io.github.leaguelugas.springmc.gui

import org.bukkit.inventory.ItemStack

data class Page(
    val slots: MutableList<GuiItem>,
    var currentPage: Int,
    var title: String? = null,
    var nextPage: Page? = null,
    var previousPage: Page? = null,
)

abstract class PageableGUI(
    slotSize: Int,
    title: String,
) : GUI(slotSize, title) {
    private var currentPage: Int = 0
    private val pages: MutableList<Page> = mutableListOf()

    fun getCurrentPage(): Int = currentPage

    fun getPages(): List<Page> = pages

    fun hasPage(pageNum: Int): Boolean = pageNum in pages.indices

    fun addPage() {
        val previousPage = pages.lastOrNull()
        val newPage =
            Page(
                slots = mutableListOf(),
                currentPage = pages.size,
                previousPage = previousPage,
            )
        pages.add(newPage)
        previousPage?.nextPage = newPage
    }

    fun addItem(
        pageNum: Int,
        slot: Int,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        while (pages.size <= pageNum) addPage()
        val guiItem = GuiItem(pageNum, slot, itemStack)
        pages[pageNum].slots.add(guiItem)
        clickMap[guiItem] = onClick
    }

    fun addItem(
        pageNum: Int,
        slots: List<Int>,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        slots.forEach { addItem(pageNum, it, itemStack, onClick) }
    }

    fun getClickLambda(
        page: Int,
        slot: Int,
        itemStack: ItemStack,
    ): InventoryHandler? = clickMap[GuiItem(page, slot, itemStack)]

    fun renderNextPage() {
        if (currentPage < pages.size - 1) {
            currentPage++
            renderGUI(currentPage)
        }
    }

    fun renderPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            renderGUI(currentPage)
        }
    }

    fun renderGUI(page: Int) {
        if (!hasPage(page)) return
        clearPage()
        val targetPage = pages[page]
        targetPage.title?.let { setTitle(it) }
        targetPage.slots.forEach { inventory.setItem(it.slot, it.itemStack) }
    }
}
