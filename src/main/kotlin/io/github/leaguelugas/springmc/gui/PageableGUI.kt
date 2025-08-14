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
    private var pages: MutableList<Page> = mutableListOf()

    fun getCurrentPage(): Int = currentPage

    fun getPages(): List<Page> = pages

    fun hasPage(pageNum: Int): Boolean = pages.size > pageNum

    fun addPage() {
        val previousPage = if (currentPage > 0) pages[currentPage - 1] else null
        pages.add(
            Page(
                slots = mutableListOf(),
                currentPage = pages.size,
                previousPage = previousPage,
            ),
        )
        if (previousPage != null) previousPage.nextPage = pages.last()
    }

    fun addItem(
        pageNum: Int,
        slot: Int,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        if (!hasPage(pageNum)) {
            while (pages.size <= pageNum) {
                addPage()
            }
        }
        val guiItem =
            GuiItem(
                page = pageNum,
                slot = slot,
                itemStack = itemStack,
            )
        pages[pageNum].slots.add(guiItem)
        clickMap[guiItem] = onClick
    }

    fun addItem(
        pageNum: Int,
        slots: List<Int>,
        itemStack: ItemStack,
        onClick: InventoryHandler? = null,
    ) {
        for (slot in slots) {
            addItem(pageNum, slot, itemStack, onClick)
        }
    }

    fun getClickLambda(
        page: Int,
        slot: Int,
        itemStack: ItemStack,
    ): InventoryHandler? {
        val guiItem = GuiItem(page, slot, itemStack)
        return clickMap[guiItem]
    }

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
        clearPage()
        val currentPage = pages[page]
        currentPage.title?.let {
            setTitle(it)
        }
        for (guiItem in currentPage.slots) {
            inventory.setItem(guiItem.slot, guiItem.itemStack)
        }
    }
}
