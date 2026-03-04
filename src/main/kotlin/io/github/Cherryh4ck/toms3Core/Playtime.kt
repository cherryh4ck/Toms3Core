package io.github.Cherryh4ck.toms3Core

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import kotlin.time.Duration.Companion.milliseconds

class Playtime(private val plugin: Toms3Core) : CommandExecutor {
    val minimessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        // no .yaml porqe tengo paja
        val targetUser : String
        val userLocale : String
        val isSpanish : Boolean

        if (sender is Player){
            userLocale = sender.locale().toString()
            isSpanish = userLocale.startsWith("es")
            targetUser = if (args.isEmpty()) {
                sender.name
            } else{
                args[0]
            }
        }
        else{
            userLocale = "es"
            isSpanish = true
            if (args.isEmpty()){
                plugin.sendError("No puedes ejecutar este comando sin poner el nombre de un jugador.")
                return true
            }
            else {
                targetUser = args[0]
            }
        }

        if (targetUser.length !in 3..16) {
            val mensaje = if (isSpanish) { minimessage.deserialize("<red>$targetUser no es un nombre de jugador válido.</red>") } else { minimessage.deserialize("<red>$targetUser is not a valid player name.</red>") }
            sender.sendMessage(mensaje)
            return true
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            // m cago en todo
            // que lio es esto con los premium en un servidor cracked wtf
            val offlineplayer = getOfflinePlayer(targetUser)
            if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L){
                val mensaje = if (isSpanish) { minimessage.deserialize("<red>$targetUser nunca entró al servidor o tiene un UUID premium.</red>") } else { minimessage.deserialize("<red>$targetUser has never entered the server or has a premium UUID.</red>") }
                sender.sendMessage(mensaje)
                return@Runnable
            }

            val statTicks = offlineplayer.getStatistic(org.bukkit.Statistic.PLAY_ONE_MINUTE).toLong()
            val ms = statTicks * 50
            val result = ms.milliseconds

            val mensaje = if (isSpanish) { minimessage.deserialize("<gold>$targetUser tiene un tiempo de juego de <bold>$result</bold>.</gold>") } else { minimessage.deserialize("<gold>$targetUser has a playtime of <bold>$result</bold>.</gold>") }
            sender.sendMessage(mensaje)
        })

        return true
    }
}