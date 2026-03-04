package io.github.Cherryh4ck.toms3Core

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit.getOfflinePlayer

import java.text.SimpleDateFormat
import java.util.Date

class Joindate(private val plugin: Toms3Core) : CommandExecutor {
    val minimessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
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
            val offlineplayer = getOfflinePlayer(targetUser)

            if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L) {
                val message = if (isSpanish){
                    minimessage.deserialize("<red>${offlineplayer.name} nunca entró al servidor o tiene un UUID premium.</red>")
                }
                else{
                    minimessage.deserialize("<red>${offlineplayer.name} has never entered the server or has a premium UUID.</red>")
                }

                sender.sendMessage(message)
                return@Runnable
            }

            val unixTime = offlineplayer.firstPlayed
            val format = if (isSpanish) { SimpleDateFormat("dd/MM/yyyy HH:mm") } else { SimpleDateFormat("MM/dd/yyyy hh:mm a") }
            val result = format.format(Date(unixTime))

            val message = if (isSpanish){
                minimessage.deserialize("<gold>${offlineplayer.name} se unió al servidor el <bold>${result}</bold>.</gold>")
            }
            else{
                minimessage.deserialize("<gold>${offlineplayer.name} joined the server on <bold>${result}</bold>.</gold>")
            }

            sender.sendMessage(message)
        })
        return true
    }
}