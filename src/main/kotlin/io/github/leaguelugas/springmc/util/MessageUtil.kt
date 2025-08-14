package io.github.leaguelugas.springmc.util

import org.bukkit.ChatColor

fun String.color() = ChatColor.translateAlternateColorCodes('&', this)

fun Long.formatNumberWithCommas(): String {
    val formattedNumber = StringBuilder()
    val numberStr = this.toString()

    val length = numberStr.length
    var counter = 0

    for (i in length - 1 downTo 0) {
        formattedNumber.insert(0, numberStr[i])
        counter++

        if (counter % 3 == 0 && i != 0) {
            formattedNumber.insert(0, ',')
        }
    }

    return formattedNumber.toString()
}
