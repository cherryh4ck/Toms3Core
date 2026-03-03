package io.github.Cherryh4ck.toms3Core

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

import kotlin.time.Duration.Companion.milliseconds

class Playtime(private val plugin: Toms3Core) : CommandExecutor {
    val minimessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val userLocale = sender.locale().toString()
            val isSpanish = userLocale.startsWith("es") // no .yaml porqe tengo paja

            val targetUser = if (args.isEmpty()) {
                sender.name
            } else{
                args[0]
            }

            if (targetUser.length < 3) {
                val mensaje = if (isSpanish) { minimessage.deserialize("<red>$targetUser no es un nombre de jugador válido.</red>") } else { minimessage.deserialize("<red>$targetUser is not a valid player name.</red>") }
                sender.sendMessage(mensaje)
                return true
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                val offlineplayer = getOfflinePlayer(targetUser)
                if (!offlineplayer.hasPlayedBefore() && !offlineplayer.isOnline){
                    val mensaje = if (isSpanish) { minimessage.deserialize("<red>$targetUser nunca entró al servidor.</red>") } else { minimessage.deserialize("<red>$targetUser has never entered the server.</red>") }
                    sender.sendMessage(mensaje)
                    return@Runnable
                }

                val statTicks = offlineplayer.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE).toLong()
                val ms = statTicks * 50
                val result = ms.milliseconds

                val mensaje = if (isSpanish) { minimessage.deserialize("<gold>$targetUser tiene un tiempo de juego de <bold>$result</bold>.</gold>") } else { minimessage.deserialize("<gold>$targetUser has a playtime of <bold>$result</bold>.</gold>") }
                sender.sendMessage(mensaje)
            })
        }
        else{
            plugin.sendError("Debes ser un jugador para ejecutar este comando.")
        }
        return true
    }
}
