package io.github.leaguelugas.springmc.util

import org.bukkit.ChatColor

class MessageUtil {
    companion object {
        fun color(message: String): String = ChatColor.translateAlternateColorCodes('&', message)

        fun formatNumberWithCommas(number: Long): String {
            val formattedNumber = StringBuilder()
            val numberStr = number.toString()

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
    }
}
