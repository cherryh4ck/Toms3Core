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

class LastSeen(private val plugin : Toms3Core) : TabExecutor {
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
        }
        else{
            isSpanish = true
        }

        if (args.isEmpty()){
            val mensaje = if (isSpanish && plugin.spanish_enabled) {
                minimessage.deserialize("${plugin.prefix} <red>No puedes usar este comando sin poner el nombre de un jugador.</red>")
            }
            else {
                minimessage.deserialize("${plugin.prefix} <red>You cannot use this command without putting the name of a player.</red>")
            }
            sender.sendMessage(mensaje)
            return true
        }
        else {
            targetUser = args[0]
        }

        if (!validateUsername(targetUser)) {
            val mensaje = if (isSpanish && plugin.spanish_enabled) {
                minimessage.deserialize("${plugin.prefix} <red>$targetUser no es un nombre de jugador válido.</red>")
            }
            else {
                minimessage.deserialize("${plugin.prefix} <red>$targetUser is not a valid player name.</red>")
            }
            sender.sendMessage(mensaje)
            return true
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val playerDataCache = File(plugin.playerDataPath, "${targetUser.lowercase()}.yml")
            val player = Bukkit.getPlayerExact(targetUser)
            if (!playerDataCache.exists()) {
                val message = if (isSpanish && plugin.spanish_enabled){
                    minimessage.deserialize("${plugin.prefix} <red>${targetUser} nunca entró al servidor o no está en el cache del servidor.</red>")
                }
                else{
                    minimessage.deserialize("${plugin.prefix} <red>${targetUser} has never entered the server or is not in the server cache.</red>")
                }

                sender.sendMessage(message)
                return@Runnable
            }
            else if (player != null && player.isConnected){
                val message = if (isSpanish && plugin.spanish_enabled){
                    minimessage.deserialize("${plugin.prefix} <red>No puedes usar este comando porque ${targetUser} está conectado.</red>")
                }
                else{
                    minimessage.deserialize("${plugin.prefix} <red>You cannot use this command because ${targetUser} is connected.</red>")
                }

                sender.sendMessage(message)
                return@Runnable
            }
            val config = YamlConfiguration.loadConfiguration(playerDataCache)

            val unixTime = config.getLong("last-seen")
            val configUuid = config.getString("uuid")
            val username : String
            if (configUuid == null) {
                plugin.logToConsole("<yellow>UUID value is null.")
                username = targetUser
            }
            else{
                val uuid = java.util.UUID.fromString(configUuid)
                username = Bukkit.getOfflinePlayer(uuid).name.toString()
                plugin.logToConsole("<green>UUID file found: ${uuid.toString()}")
            }
            if (unixTime.toInt() != 0){
                val format = if (isSpanish && plugin.spanish_enabled) {
                    SimpleDateFormat("dd/MM/yyyy HH:mm")
                } else {
                    SimpleDateFormat("MM/dd/yyyy hh:mm a")
                }
                val result = format.format(Date(unixTime))

                val message = if (isSpanish && plugin.spanish_enabled){
                    minimessage.deserialize("${plugin.prefix} <gold><bold>${username}</bold> fue visto por última vez el <bold>${result}</bold>.</gold>")
                }
                else{
                    minimessage.deserialize("${plugin.prefix} <gold><bold>${username}</bold> was last seen on <bold>${result}</bold>.</gold>")
                }

                sender.sendMessage(message)
            }
            else{
                val message = if (isSpanish && plugin.spanish_enabled){
                    minimessage.deserialize("${plugin.prefix} <red>${targetUser} nunca entró al servidor o no está en el cache del servidor.</red>")
                }
                else{
                    minimessage.deserialize("${plugin.prefix} <red>${targetUser} has never entered the server or is not in the server cache.</red>")
                }

                sender.sendMessage(message)
                return@Runnable
            }
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