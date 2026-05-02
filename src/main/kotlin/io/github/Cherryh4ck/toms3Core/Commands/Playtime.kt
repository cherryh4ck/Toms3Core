package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class Playtime(private val plugin: Toms3Core) : TabExecutor {
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
                plugin.logToConsole("<red>You need to specify a player to use this command.")
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
                plugin.logToConsole("<red>$targetUser's UUID file doesn't exist.")
                Bukkit.getOfflinePlayer(targetUser)
            }
            else{
                val config = YamlConfiguration.loadConfiguration(playerDataCache)
                val configUuid = config.getString("uuid")
                if (configUuid == null) {
                    plugin.logToConsole("<yellow>UUID value is null.")
                    Bukkit.getOfflinePlayer(targetUser)
                }
                else{
                    val uuid = java.util.UUID.fromString(configUuid)
                    plugin.logToConsole("<green>UUID file found: ${uuid.toString()}")
                    Bukkit.getOfflinePlayer(uuid)
                }
            }
            if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L){
                val mensaje = if (isSpanish) {
                    minimessage.deserialize("${plugin.prefix} <red>$targetUser nunca entró al servidor o no está en el cache del servidor.</red>")
                }
                else {
                    minimessage.deserialize("${plugin.prefix} <red>$targetUser has never entered the server or is not in the server cache.</red>")
                }
                sender.sendMessage(mensaje)
                return@Runnable
            }

            val statTicks = offlineplayer.getStatistic(Statistic.PLAY_ONE_MINUTE).toLong()
            val ms = statTicks * 50
            val result = ms.milliseconds
            val resultHs = "%.2f".format(result.toDouble(DurationUnit.HOURS))

            val mensaje = if (isSpanish) {
                if (offlineplayer.name != sender.name){
                    minimessage.deserialize("<gold>${plugin.prefix} <bold>$targetUser</bold> tiene un tiempo de juego de <bold>$result</bold> (<bold>${resultHs}hs</bold>).</gold>")
                }
                else{
                    minimessage.deserialize("<gold>${plugin.prefix} Tienes un tiempo de juego de <bold>$result</bold> (<bold>${resultHs}hs</bold>).</gold>")
                }
            }
            else {
                if (offlineplayer.name != sender.name){
                    minimessage.deserialize("<gold>${plugin.prefix} <bold>$targetUser</bold> has a playtime of <bold>$result</bold> (<bold>${resultHs}hs</bold>).</gold>")
                }
                else{
                    minimessage.deserialize("<gold>${plugin.prefix} You have a playtime of <bold>$result</bold> (<bold>${resultHs}hs</bold>).</gold>")
                }
            }
            sender.sendMessage(mensaje)
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