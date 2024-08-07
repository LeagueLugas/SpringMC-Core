package io.github.leaguelugas.springmc.command

import org.bukkit.command.CommandSender

interface SpringCommand {
    fun execute(
        sender: CommandSender,
        args: Array<String>,
    )

    fun <T : CommandSender> tabComplete(
        sender: T,
        alias: String,
        args: Array<String>,
    ): List<String> = emptyList()

    fun errorConsoleOnly(): String = "&4You are not allowed to perform this action as a player."

    fun errorPlayerOnly(): String = "&4This command cannot be executed from the console."

    fun errorPermission(): String = "&4You do not have permission to perform this command."
}
