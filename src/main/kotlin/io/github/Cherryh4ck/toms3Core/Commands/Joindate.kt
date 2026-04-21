package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class Joindate(private val plugin: Toms3Core) : TabExecutor {
    val minimessage = MiniMessage.miniMessage()

    fun validateUsername(username: String): Boolean {
        val regex = Regex(plugin.usernameValidationRegex)
        return regex.matches(username)
    }

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
            isSpanish = true
            if (args.isEmpty()){
                plugin.sendError("You need to specify a player to use this command.")
                return true
            }
            else {
                targetUser = args[0]
            }
        }

        if (!validateUsername(targetUser)) {
            val mensaje = if (isSpanish) {
                minimessage.deserialize("${plugin.prefix} <red>$targetUser no es un nombre de jugador válido.</red>")
            }
            else {
                minimessage.deserialize("${plugin.prefix} <red>$targetUser is not a valid player name.</red>")
            }
            sender.sendMessage(mensaje)
            return true
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val playerDataCache = File(plugin.playerDataPath, "${targetUser}.yml")
            val offlineplayer = if (!playerDataCache.exists()) {
                plugin.logger.info("$targetUser's UUID file doesn't exist.")
                Bukkit.getOfflinePlayer(targetUser)
            }
            else{
                val config = YamlConfiguration.loadConfiguration(playerDataCache)
                val uuid = java.util.UUID.fromString(config.getString("uuid"))
                plugin.logger.info("UUID file found: ${uuid.toString()}")
                Bukkit.getOfflinePlayer(uuid)
            }

            if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L) {
                val message = if (isSpanish){
                    minimessage.deserialize("${plugin.prefix} <red>${offlineplayer.name} nunca entró al servidor o no está en el cache del servidor.</red>")
                }
                else{
                    minimessage.deserialize("${plugin.prefix} <red>${offlineplayer.name} has never entered the server or is not in the server cache.</red>")
                }

                sender.sendMessage(message)
                return@Runnable
            }

            val unixTime = offlineplayer.firstPlayed
            val format = if (isSpanish) {
                SimpleDateFormat("dd/MM/yyyy HH:mm")
            } else {
                SimpleDateFormat("MM/dd/yyyy hh:mm a")
            }
            val result = format.format(Date(unixTime))

            val message = if (isSpanish){
                if (offlineplayer.name != sender.name){
                    minimessage.deserialize("<gold>${plugin.prefix} <bold>${offlineplayer.name}</bold> se unió al servidor el <bold>${result}</bold>.</gold>")
                }
                else{
                    minimessage.deserialize("<gold>${plugin.prefix} Te uniste al servidor el <bold>${result}</bold>.</gold>")
                }
            }
            else{
                if (offlineplayer.name != sender.name){
                    minimessage.deserialize("<gold>${plugin.prefix} <bold>${offlineplayer.name}</bold> joined the server on <bold>${result}</bold>.</gold>")
                }
                else{
                    minimessage.deserialize("<gold>${plugin.prefix} You joined the server on <bold>${result}</bold>.</gold>")
                }
            }

            sender.sendMessage(message)
        })
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return if (args.size == 1){
            Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        else{
            emptyList()
        }
    }
}