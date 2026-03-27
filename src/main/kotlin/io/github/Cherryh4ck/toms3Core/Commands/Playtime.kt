package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

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
            val playerDataCache = File(plugin.playerDataPath, "${targetUser}.yml")
            val offlineplayer = if (!playerDataCache.exists()) {
                plugin.logger.info("No existe el archivo playerdata para $targetUser.")
                Bukkit.getOfflinePlayer(targetUser)
            }
            else{
                val config = YamlConfiguration.loadConfiguration(playerDataCache)
                val uuid = java.util.UUID.fromString(config.getString("uuid"))
                plugin.logger.info("Existe el archivo playerdata de $targetUser, UUID conseguido: ${uuid.toString()}")
                Bukkit.getOfflinePlayer(uuid)
            }
            if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L){
                val mensaje = if (isSpanish) { minimessage.deserialize("<red>$targetUser nunca entró al servidor o no está en el cache del servidor.</red>") } else { minimessage.deserialize("<red>$targetUser has never entered the server or is not in the server cache.</red>") }
                sender.sendMessage(mensaje)
                return@Runnable
            }

            val statTicks = offlineplayer.getStatistic(Statistic.PLAY_ONE_MINUTE).toLong()
            val ms = statTicks * 50
            val result = ms.milliseconds
            val resultHs = "%.2f".format(result.toDouble(DurationUnit.HOURS))

            val mensaje = if (isSpanish) { minimessage.deserialize("<gold><bold>$targetUser</bold> tiene un tiempo de juego de <bold>$result</bold> (<bold>${resultHs}hs</bold>).</gold>") } else { minimessage.deserialize("<gold><bold>$targetUser</bold> has a playtime of <bold>$result</bold> (<bold>${resultHs}hs</bold>).</gold>") }
            sender.sendMessage(mensaje)
        })

        return true
    }
}