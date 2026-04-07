package io.github.Cherryh4ck.toms3Core

import io.github.Cherryh4ck.toms3Core.Commands.Dupe
import io.github.Cherryh4ck.toms3Core.Commands.Joindate
import io.github.Cherryh4ck.toms3Core.Commands.LastSeen
import io.github.Cherryh4ck.toms3Core.Commands.Playtime
import io.github.Cherryh4ck.toms3Core.Commands.Vote
import io.github.Cherryh4ck.toms3Core.Listeners.CachePlayerJoinListener
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerInteractIllegalListener
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerJoinListener
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerLeaveListener
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerNetherRoofListener
import io.github.Cherryh4ck.toms3Core.Listeners.BlockPlaceListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class Toms3Core : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()

    var prefix = config.getString("general.prefix")
    var discordWebhook = config.getString("general.discord-webhook")

    var usernameValidationRegex = config.getString("username-validation-regex") ?: "^[a-zA-Z0-9_]{3,16}\$"

    var tile_entities_limit = config.getInt("chunk-limits.tile-entities")

    var illegals_prevent_use = config.getStringList("illegals.prevent-use").map { it.uppercase() }
    var illegals_prevent_place = config.getStringList("illegals.prevent-place").map { it.uppercase() }

    var motd_general = config.getString("misc.join-motd.general.en")
    var motd_es = config.getString("misc.join-motd.general.es")

    var first_join_motd = config.getString("misc.join-motd.first-join.en")
    var first_join_motd_es = config.getString("misc.join-motd.first-join.es")

    var title_announcement_en = config.getString("misc.join-title-message.en")
    var title_announcement_es = config.getString("misc.join-title-message.es")

    var announcements_timer = config.getInt("misc.announcements.timer")
    var announcements_interval = (20 * announcements_timer).toLong()
    var announcements = config.getConfigurationSection("misc.announcements.messages")
    val playerDataPath = File(dataFolder, "playerdata")

    override fun onEnable() {
        logger.info("---------------------")
        saveDefaultConfig()
        if (!playerDataPath.exists()) {
            logger.info("Playerdata creado.")
            playerDataPath.mkdirs()
        }

        val tmcore = getCommand("tmcore")
        tmcore?.setExecutor(this)
        tmcore?.tabCompleter = this

        getCommand("playtime")?.setExecutor(Playtime(this))
        getCommand("pt")?.setExecutor(Playtime(this))
        getCommand("dupe")?.setExecutor(Dupe(this))
        getCommand("joindate")?.setExecutor(Joindate(this))
        getCommand("jd")?.setExecutor(Joindate(this))
        getCommand("lastseen")?.setExecutor(LastSeen(this))
        getCommand("ls")?.setExecutor(LastSeen(this))
        getCommand("vote")?.setExecutor(Vote(this))

        server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        server.pluginManager.registerEvents(PlayerLeaveListener(this), this)
        server.pluginManager.registerEvents(CachePlayerJoinListener(this), this)
        server.pluginManager.registerEvents(PlayerNetherRoofListener(this), this)
        server.pluginManager.registerEvents(BlockPlaceListener(this), this)
        server.pluginManager.registerEvents(PlayerInteractIllegalListener(this), this)

        logger.info("Core activado.")
        logger.info("---------------------")

        sendAnnouncements()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sendError(message : String){
        logger.warning(message)
    }

    fun reloadPlugin(){
        reloadConfig()
        prefix = config.getString("general.prefix")
        discordWebhook = config.getString("general.discord-webhook")
        tile_entities_limit = config.getInt("chunk-limits.tile-entities")
        illegals_prevent_use = config.getStringList("illegals.prevent-use").map { it.uppercase() }
        illegals_prevent_place = config.getStringList("illegals.prevent-place").map { it.uppercase() }
        motd_general = config.getString("misc.join-motd.general.en")
        motd_es = config.getString("misc.join-motd.general.es")
        first_join_motd = config.getString("misc.join-motd.first-join.en")
        first_join_motd_es = config.getString("misc.join-motd.first-join.es")
        title_announcement_en = config.getString("misc.join-title-message.en")
        title_announcement_es = config.getString("misc.join-title-message.es")
        announcements_timer = config.getInt("misc.announcements.timer")
        announcements_interval = (20 * announcements_timer).toLong()
        announcements = config.getConfigurationSection("misc.announcements.messages")
    }

    fun sendAnnouncements(){
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            announcements?.let { announcement ->
                val list = announcement.getKeys(false).toList()
                if (list.isNotEmpty()) {
                    val random = list.random()
                    val path = "misc.announcements.messages.$random"
                    val msgEn = config.getString("$path.en")?.let { input -> minimessage.deserialize(input) }
                        ?: Component.empty()
                    val msgEs = config.getString("$path.es")?.let { input -> minimessage.deserialize(input) }
                        ?: Component.empty()

                    for (player in Bukkit.getOnlinePlayers()) {
                        val locale = player.locale().toString()
                        if (locale.startsWith("es")) {
                            player.sendMessage(msgEs)
                        } else {
                            player.sendMessage(msgEn)
                        }
                    }
                }
            }
        }, 0L, announcements_interval)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val mensaje : Component
        if(args.isEmpty()){
            mensaje = minimessage.deserialize("<gold>$prefix Plugin corriendo - versión ${this.pluginMeta.version} (actualización n. 8)</gold>")
            sender.sendMessage(mensaje)
            return true
        }

        when (args[0].lowercase()) {
            "help" -> {
                mensaje = minimessage.deserialize("$prefix <gold>Comandos disponibles:<newline>- <gray>/tmcore</gray><newline>- <gray>/tmcore get_player_by_uuid</gray><newline>- <gray>/tmcore get_uuid_by_player</gray><newline>- <gray>/tmcore illegal_test</gray><newline>- <gray>/joindate</gray><newline>- <gray>/playtime</gray><newline>- <gray>/lastseen</gray><newline>- <gray>/dupe</gray><newline>- <gray>/vote</gray></gold>")
                sender.sendMessage(mensaje)
            }
            "reload" -> {
                reloadPlugin()
                mensaje = minimessage.deserialize("$prefix <gold>Plugin recargado.</gold>")
                sender.sendMessage(mensaje)
            }
            "illegal_test" -> {
                // terminar
                // ok, nunca lo voy a terminar, soy un vago.
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
            "migrate_fallen_dupe" -> {
                val dupeList = config.getStringList("fallen-for-dupe")
                val dupeListLength = dupeList.size
                if (dupeListLength > 0){
                    sender.sendMessage(minimessage.deserialize("$prefix <green>$dupeListLength usuarios conseguidos."))
                    sender.sendMessage(minimessage.deserialize("$prefix <green>Comenzando migración, por favor mire la consola para ver el progreso."))
                    Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {
                        for ((index, user) in dupeList.withIndex()) {
                            val uuid: UUID = UUID.fromString(user)
                            val offlinePlayer = getOfflinePlayer(uuid)
                            val playerData = File(playerDataPath, "${offlinePlayer.name}.yml")
                            val config = YamlConfiguration.loadConfiguration(playerData)
                            config.set("fallen-for-dupe", true)
                            try {
                                config.save(playerData)
                                logger.info("[${index + 1}/$dupeListLength] ${offlinePlayer.name} migrado a ${playerData.path}")
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                        sender.sendMessage(minimessage.deserialize("$prefix <green>Finalizado."))
                    })
                }
                else{
                    mensaje = minimessage.deserialize("$prefix <red>No hay usuarios que migrar.</red>")
                    sender.sendMessage(mensaje)
                }
            }
            else -> {
                sender.sendMessage(minimessage.deserialize("$prefix <red>Ese comando no existe.</red>"))
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            val subs = listOf("help", "reload", "illegal_test", "get_uuid_by_player", "get_player_by_uuid", "migrate_fallen_dupe")
            for (s in subs) {
                if (s.startsWith(args[0].lowercase())) {
                    completions.add(s)
                }
            }
        }
        return completions
    }
}