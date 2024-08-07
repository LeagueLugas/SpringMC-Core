package io.github.leaguelugas.springmc.di.resolvers

import io.github.leaguelugas.springmc.SpringMC
import io.github.leaguelugas.springmc.command.SpringCommand
import io.github.leaguelugas.springmc.di.BeanResolver
import io.github.leaguelugas.springmc.di.annotations.Command
import io.github.leaguelugas.springmc.util.MessageUtil
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class CommandResolver(
    private val plugin: SpringMC,
) : BeanResolver<Command> {
    private val commandMap: CommandMap =
        plugin.server
            .javaClass
            .getDeclaredField("commandMap")
            .apply {
                isAccessible = true
            }.get(plugin.server) as CommandMap

    override fun resolveBean(
        instance: Any,
        annotation: Command,
    ): Any {
        val commandClass = "${instance.javaClass.`package`}.${instance.javaClass.simpleName}"
        if (instance !is SpringCommand) {
            throw IllegalArgumentException(
                "Command ($commandClass) must implement SpringCommand interface",
            )
        }
        if (annotation.command.isBlank()) {
            throw IllegalArgumentException(
                "Command name cannot be blank ($commandClass)",
            )
        }

        val bukkitCommand =
            object : BukkitCommand(annotation.command) {
                init {
                    description = annotation.description
                    usageMessage = annotation.usage
                    aliases = annotation.aliases.toMutableList()
                }

                override fun execute(
                    sender: CommandSender,
                    commandLabel: String,
                    args: Array<String>,
                ): Boolean {
                    if (sender is Player && annotation.type == Command.Type.CONSOLE_ONLY) {
                        sender.sendMessage(MessageUtil.color(instance.errorConsoleOnly()))
                        return false
                    } else if (sender is ConsoleCommandSender && annotation.type == Command.Type.PLAYER_ONLY) {
                        sender.sendMessage(MessageUtil.color(instance.errorPlayerOnly()))
                        return false
                    }

                    if (annotation.permissions.any { sender.hasPermission(it) }) {
                        runCatching {
                            instance.execute(sender, args)
                        }.onFailure { exception: Throwable ->
                            if (exception is ClassCastException) {
                                plugin.logger.warning(
                                    "Command sender class mismatch in Command ($commandClass)",
                                )
                            }
                            exception.printStackTrace()
                        }
                    } else {
                        sender.sendMessage(MessageUtil.color(instance.errorPermission()))
                    }
                    return true
                }

                override fun tabComplete(
                    sender: CommandSender,
                    alias: String,
                    args: Array<String>,
                ): List<String> {
                    runCatching {
                        if (annotation.permissions.any { sender.hasPermission(it) }) {
                            return instance.tabComplete(sender, alias, args)
                        }
                    }.onFailure { it.printStackTrace() }
                    return emptyList()
                }
            }

        commandMap.register(plugin.name, bukkitCommand)
        return instance
    }
}
