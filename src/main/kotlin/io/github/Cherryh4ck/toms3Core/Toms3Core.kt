package io.github.Cherryh4ck.toms3Core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class Toms3Core : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()
    val prefix = "<gold>[<red><bold>Toms3<white>Core</white></bold></red>]"

    var discordWebhook = config.getString("discord-webhook")

    var motd_general = config.getString("motd-general")
    var motd_es = config.getString("motd-es")
    var first_join_motd = config.getString("first-join-motd")
    var first_join_motd_es = config.getString("first-join-motd-es")

    override fun onEnable() {
        saveDefaultConfig()

        val tmcore = getCommand("tmcore")
        tmcore?.setExecutor(this)
        tmcore?.tabCompleter = this

        getCommand("playtime")?.setExecutor(Playtime(this))
        getCommand("pt")?.setExecutor(Playtime(this))
        getCommand("dupe")?.setExecutor(DupeJoke(this))
        getCommand("joindate")?.setExecutor(Joindate(this))
        getCommand("jd")?.setExecutor(Joindate(this))

        server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        server.pluginManager.registerEvents(PlayerNetherRoofListener(this), this)

        logger.info("---------------------")
        logger.info("Core activado.")
        logger.info("---------------------")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sendError(message : String){
        logger.warning(message)
    }

    fun reloadPlugin(){
        reloadConfig()
        discordWebhook = config.getString("discord-webhook")
        motd_general = config.getString("motd-general")
        motd_es = config.getString("motd-es")
        first_join_motd = config.getString("first-join-motd")
        first_join_motd_es = config.getString("first-join-motd-es")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val mensaje : Component
        if(args.isEmpty()){
            mensaje = minimessage.deserialize("<gold>$prefix Plugin corriendo - versión 1.0 (revisión n. 5)</gold>")
            sender.sendMessage(mensaje)
            return true
        }

        when (args[0].lowercase()) {
            "help" -> {
                mensaje = minimessage.deserialize("$prefix <gold>Comandos disponibles:<newline>- <gray>/tmcore</gray><newline>- <gray>/tmcore get_player_by_uuid</gray><newline>- <gray>/tmcore get_uuid_by_player</gray><newline>- <gray>/tmcore illegal_test</gray><newline>- <gray>/joindate</gray><newline>- <gray>/playtime</gray><newline>- <gray>/dupe</gray></gold>")
                sender.sendMessage(mensaje)
            }
            "reload" -> {
                reloadPlugin()
                mensaje = minimessage.deserialize("$prefix <gold>Plugin recargado.</gold>")
                sender.sendMessage(mensaje)
            }
            "illegal_test" -> {
                // terminar
                mensaje = if (sender is Player) {
                    minimessage.deserialize("$prefix <red>popbob</red>")
                }
                else{
                    minimessage.deserialize("$prefix <red>Debes ser un jugador para usar este comando.</red>")
                }
                sender.sendMessage(mensaje)
            }
            "get_uuid_by_player" -> {
                if (sender !is Player && args.size > 1) {
                    mensaje = minimessage.deserialize("$prefix <red>Debes especificar un jugador para usar este comando.</red>")
                    sender.sendMessage(mensaje)
                    return true
                }

                val targetUser = if (args.size < 2) {
                    sender.name
                }
                else{
                    args[1]
                }

                val offlineplayer = getOfflinePlayer(targetUser)

                if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L) {
                    mensaje = minimessage.deserialize("$prefix <red>Este jugador nunca estuvo en el servidor o tiene un UUID premium.</red>")
                    sender.sendMessage(mensaje)
                    return true
                }

                val uuid = offlineplayer.uniqueId
                mensaje = minimessage.deserialize("$prefix <gold>El UUID de $targetUser es <bold><click:copy_to_clipboard:$uuid>$uuid</click></bold>.</gold>")
                sender.sendMessage(mensaje)
            }
            "get_player_by_uuid" -> {
                if (args.size < 2) {
                    mensaje = minimessage.deserialize("$prefix <red>Debes especificar la UUID de un jugador para usar este comando.</red>")
                    sender.sendMessage(mensaje)
                    return true
                }

                val uuidString = args[1]
                val uuid: UUID = UUID.fromString(uuidString)

                val offlineplayer = getOfflinePlayer(uuid)

                if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L) {
                    mensaje = minimessage.deserialize("$prefix <red>Este jugador nunca estuvo en el servidor o tiene un UUID premium.</red>")
                    sender.sendMessage(mensaje)
                }
                else{
                    mensaje = minimessage.deserialize("$prefix <gold>El usuario es <bold>${offlineplayer.name}</bold>.</gold>")
                    sender.sendMessage(mensaje)
                }
            }
            else -> {
                mensaje = minimessage.deserialize("$prefix <red>Ese comando no existe.</red>")
                sender.sendMessage(mensaje)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            val subs = listOf("help", "reload", "illegal_test", "get_uuid_by_player", "get_player_by_uuid")
            for (s in subs) {
                if (s.startsWith(args[0].lowercase())) {
                    completions.add(s)
                }
            }
        }
        return completions
    }
}
