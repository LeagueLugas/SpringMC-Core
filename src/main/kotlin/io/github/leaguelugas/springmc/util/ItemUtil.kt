package io.github.leaguelugas.springmc.util

import org.bukkit.inventory.ItemStack

fun ItemStack.setName(name: String) =
    apply {
        this.itemMeta =
            this.itemMeta?.apply {
                setDisplayName(name)
            }
    }

fun ItemStack.setLore(lore: List<String>) =
    apply {
        this.itemMeta =
            this.itemMeta?.apply {
                setLore(lore)
            }
    }

fun ItemStack.addLore(
    lore: String,
    index: Int?,
) = apply {
    this.itemMeta =
        this.itemMeta?.apply {
            if (index != null) {
                this.lore?.add(index, lore)
            } else {
                this.lore?.add(lore)
            }
        }
}

fun ItemStack.removeLore(index: Int?) =
    apply {
        this.itemMeta =
            this.itemMeta?.apply {
                if (index != null) {
                    this.lore?.removeAt(index)
                } else {
                    this.lore?.clear()
                }
            }
    }
