package io.github.Cherryh4ck.toms3Core

import io.github.Cherryh4ck.toms3Core.Commands.Dupe
import io.github.Cherryh4ck.toms3Core.Commands.Joindate
import io.github.Cherryh4ck.toms3Core.Commands.LastSeen
import io.github.Cherryh4ck.toms3Core.Commands.Playtime
import io.github.Cherryh4ck.toms3Core.Commands.Seed
import io.github.Cherryh4ck.toms3Core.Commands.ToggleAnnouncements
import io.github.Cherryh4ck.toms3Core.Commands.Vote
import io.github.Cherryh4ck.toms3Core.Listeners.AntiIllegals.PreventNBTStorages
import io.github.Cherryh4ck.toms3Core.Listeners.AntiIllegals.PreventPlace
import io.github.Cherryh4ck.toms3Core.Listeners.CachePlayerJoinListener
import io.github.Cherryh4ck.toms3Core.Listeners.AntiIllegals.PreventUse
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerJoinListener
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerLeaveListener
import io.github.Cherryh4ck.toms3Core.Listeners.PlayerNetherRoofListener
import io.github.Cherryh4ck.toms3Core.Listeners.BlockPlaceListener
import io.github.Cherryh4ck.toms3Core.Listeners.OPCommandBlacklistListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.util.UUID

class Toms3Core : JavaPlugin() {
    // -- IMPORTANT --
    val configVersion = 7
    // -- IMPORTANT --

    val minimessage = MiniMessage.miniMessage()

    // ** config.yml configuration ** //

    // ** general config ** //
    var prefix = config.getString("general.prefix")
    var discordWebhook = config.getString("general.discord-webhook")
    var spanish_enabled = config.getBoolean("general.enable-spanish-translation")
    var cache_system_logging_enabled = config.getBoolean("general.enable-cache-system-logging")

    var chunklimits_enable = config.getBoolean("chunk-limits.enable")

    var usernameValidationRegex = config.getString("commands.username-validation-regex") ?: "^[a-zA-Z0-9_]{3,16}\$"

    var dupe_webhook_message = config.getString("commands.dupe.discord-webhook-message")

    var vote_message_en = config.getString("commands.vote.message.en")
    var vote_message_es = config.getString("commands.vote.message.es")

    var toggleannouncements_message_on_en = config.getString("commands.toggleannouncements.message.on.en")
    var toggleannouncements_message_on_es = config.getString("commands.toggleannouncements.message.on.es")
    var toggleannouncements_message_off_en = config.getString("commands.toggleannouncements.message.off.en")
    var toggleannouncements_message_off_es = config.getString("commands.toggleannouncements.message.off.es")

    var op_blacklisted_commands = config.getStringList("utilities.op-command-blacklist.blacklisted-commands")

    var tile_entities_limit = config.getInt("chunk-limits.tile-entities.entity-limit")
    var tile_entities_log = config.getBoolean("chunk-limits.tile-entities.log")

    var illegals_prevent_use_enabled = config.getBoolean("illegals.block-use.enable")
    var illegals_prevent_use = config.getStringList("illegals.block-use.list").map { it.uppercase() }
    var illegals_prevent_place_enabled = config.getBoolean("illegals.block-place.enable")
    var illegals_prevent_place = config.getStringList("illegals.block-place.list").map { it.uppercase() }

    var block_nether_roof = config.getBoolean("patches.nether.block-nether-roof.enable")
    var patch_vclip_exploit_mode = config.getString("patches.nether.patch-vclip-exploit.mode")
    var patch_vclip_exploit = config.getBoolean("patches.nether.patch-vclip-exploit.enable")

    var motd_general = config.getString("misc.join-events.join-motd.general.en")
    var motd_es = config.getString("misc.join-events.join-motd.general.es")

    var first_join_motd = config.getString("misc.join-events.join-motd.first-join.en")
    var first_join_motd_es = config.getString("misc.join-events.join-motd.first-join.es")

    var title_announcement_en = config.getString("misc.join-events.join-title-message.en")
    var title_announcement_es = config.getString("misc.join-events.join-title-message.es")

    var actionbar_announcement_en = config.getString("misc.join-events.join-action-bar-message.en")
    var actionbar_announcement_es = config.getString("misc.join-events.join-action-bar-message.es")

    var announcements_enabled = config.getBoolean("misc.announcements.enable")
    var announcements_timer = config.getInt("misc.announcements.timer")
    var announcements_interval = (20 * announcements_timer).toLong()
    var announcements = config.getConfigurationSection("misc.announcements.messages")

    var emergent_announcements_enabled = config.getBoolean("misc.emergent-announcements.enable")
    var emergent_announcements_timer = config.getInt("misc.emergent-announcements.timer")
    var emergent_announcements_interval = (20 * emergent_announcements_timer).toLong()
    var emergent_announcements = config.getConfigurationSection("misc.emergent-announcements.messages")
    val playerDataPath = File(dataFolder, "playerdata")

    // Lists
    var haveAnnouncementsDisabled = HashSet<UUID>()

    override fun onEnable() {
        logger.info("---------------------")
        saveDefaultConfig()

        if (!playerDataPath.exists()) {
            logger.info("Playerdata folder created.")
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
        getCommand("seed")?.setExecutor(Seed(this))
        getCommand("toggleannouncements")?.setExecutor(ToggleAnnouncements(this))
        getCommand("toggleann")?.setExecutor(ToggleAnnouncements(this))

        server.pluginManager.registerEvents(PlayerLeaveListener(this), this)
        server.pluginManager.registerEvents(CachePlayerJoinListener(this), this)
        server.pluginManager.registerEvents(BlockPlaceListener(this), this)

        reloadPlugin()

        logger.info("Core activated.")
        logger.info("---------------------")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun logToConsole(message: String) {
        Bukkit.getConsoleSender().sendMessage(minimessage.deserialize("$prefix $message"))
    }

    private val listeners = mapOf(
        "illegals.block-use.enable" to PreventUse(this),
        "illegals.block-place.enable" to PreventPlace(this),
        "patches.nether.enable" to PlayerNetherRoofListener(this),
        "misc.join-events.enable" to PlayerJoinListener(this),
        "utilities.op-command-blacklist.enable" to OPCommandBlacklistListener(this),
        "illegals.block-nbt-storages.enable" to PreventNBTStorages()
    )

    fun hookListeners(){
        listeners.forEach { (path, listener) ->
            HandlerList.unregisterAll(listener)
            if (config.getBoolean(path, false)) {
                server.pluginManager.registerEvents(listener, this)
                logToConsole("<yellow>Hooked listener: <green>$path")
            }
        }
    }

    fun reloadPlugin(){
        reloadConfig()
        // This is so fucked up.
        prefix = config.getString("general.prefix")
        discordWebhook = config.getString("general.discord-webhook")
        spanish_enabled = config.getBoolean("general.enable-spanish-translation")
        cache_system_logging_enabled = config.getBoolean("general.enable-cache-system-logging")
        usernameValidationRegex = config.getString("commands.username-validation-regex") ?: "^[a-zA-Z0-9_]{3,16}\$"
        dupe_webhook_message = config.getString("commands.dupe.discord-webhook-message")
        vote_message_en = config.getString("commands.vote.message.en")
        vote_message_es = config.getString("commands.vote.message.es")
        toggleannouncements_message_on_en = config.getString("commands.toggleannouncements.message.on.en")
        toggleannouncements_message_on_es = config.getString("commands.toggleannouncements.message.on.es")
        toggleannouncements_message_off_en = config.getString("commands.toggleannouncements.message.off.en")
        toggleannouncements_message_off_es = config.getString("commands.toggleannouncements.message.off.es")
        op_blacklisted_commands = config.getStringList("utilities.op-command-blacklist.blacklisted-commands")
        chunklimits_enable = config.getBoolean("chunk-limits.enable")
        tile_entities_limit = config.getInt("chunk-limits.tile-entities.entity-limit")
        tile_entities_log = config.getBoolean("chunk-limits.tile-entities.log")
        illegals_prevent_use_enabled = config.getBoolean("illegals.block-use.enable")
        illegals_prevent_place_enabled = config.getBoolean("illegals.block-place.enable")
        illegals_prevent_use = config.getStringList("illegals.block-use.list").map { it.uppercase() }
        illegals_prevent_place = config.getStringList("illegals.block-place.list").map { it.uppercase() }
        block_nether_roof = config.getBoolean("patches.nether.block-nether-roof.enable")
        patch_vclip_exploit_mode = config.getString("patches.nether.patch-vclip-exploit.mode")
        patch_vclip_exploit = config.getBoolean("patches.nether.patch-vclip-exploit.enable")
        motd_general = config.getString("misc.join-events.join-motd.general.en")
        motd_es = config.getString("misc.join-events.join-motd.general.es")
        first_join_motd = config.getString("misc.join-events.join-motd.first-join.en")
        first_join_motd_es = config.getString("misc.join-events.join-motd.first-join.es")
        title_announcement_en = config.getString("misc.join-events.join-title-message.en")
        title_announcement_es = config.getString("misc.join-events.join-title-message.es")
        actionbar_announcement_en = config.getString("misc.join-events.join-action-bar-message.en")
        actionbar_announcement_es = config.getString("misc.join-events.join-action-bar-message.es")
        announcements_enabled = config.getBoolean("misc.announcements.enable")
        announcements_timer = config.getInt("misc.announcements.timer")
        announcements_interval = (20 * announcements_timer).toLong()
        announcements = config.getConfigurationSection("misc.announcements.messages")
        emergent_announcements_enabled = config.getBoolean("misc.emergent-announcements.enable")
        emergent_announcements_timer = config.getInt("misc.emergent-announcements.timer")
        emergent_announcements_interval = (20 * emergent_announcements_timer).toLong()
        emergent_announcements = config.getConfigurationSection("misc.emergent-announcements.messages")

        hookListeners()
        stopAnnouncements()
        stopEmergentAnnouncements()
        if (announcements_enabled){
            sendAnnouncements()
            logToConsole("<green>Announcements restarted.")
        }
        if (emergent_announcements_enabled){
            sendEmergentAnnouncements()
            logToConsole("<green>Emergent announcements restarted.")
        }
        isConfigUpdated()
    }

    fun isConfigUpdated(){
        val getConfigVersion = config.getInt("config-version")
        if (configVersion != getConfigVersion) {
            logToConsole("<yellow>Config is outdated, which means that a few things are missing. To ensure everything works fine, please redo your config.")
        }
    }

    private var announcementsTask: BukkitTask? = null
    fun sendAnnouncements(){
        announcementsTask?.cancel()
        announcementsTask = Bukkit.getScheduler().runTaskTimer(this, Runnable {
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
                        if (!haveAnnouncementsDisabled.contains(player.uniqueId)) {
                            val locale = player.locale().toString()
                            if (locale.startsWith("es") && spanish_enabled) {
                                player.sendMessage(msgEs)
                            } else {
                                player.sendMessage(msgEn)
                            }
                        }
                    }
                }
            }
        }, 0L, announcements_interval)
    }

    fun stopAnnouncements(){
        announcementsTask?.cancel()
        announcementsTask = null
    }

    private var emergentAnnouncementsTask: BukkitTask? = null
    fun sendEmergentAnnouncements(){
        emergentAnnouncementsTask?.cancel()
        emergentAnnouncementsTask = Bukkit.getScheduler().runTaskTimer(this, Runnable {
            emergent_announcements?.let { announcement ->
                val list = announcement.getKeys(false).toList()
                if (list.isNotEmpty()) {
                    val random = list.random()
                    val path = "misc.emergent-announcements.messages.$random"
                    val msgEn = config.getString("$path.en")?.let { input -> minimessage.deserialize(input) }
                        ?: Component.empty()
                    val msgEs = config.getString("$path.es")?.let { input -> minimessage.deserialize(input) }
                        ?: Component.empty()

                    for (player in Bukkit.getOnlinePlayers()) {
                        val locale = player.locale().toString()
                        if (locale.startsWith("es") && spanish_enabled) {
                            player.sendMessage(msgEs)
                        } else {
                            player.sendMessage(msgEn)
                        }
                    }
                }
            }
        }, 0L, emergent_announcements_interval)
    }

    fun stopEmergentAnnouncements(){
        emergentAnnouncementsTask?.cancel()
        emergentAnnouncementsTask = null
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val mensaje : Component
        if(args.isEmpty()){
            mensaje = minimessage.deserialize("<gold>$prefix version ${this.pluginMeta.version}</gold>")
            sender.sendMessage(mensaje)
            return true
        }

        when (args[0].lowercase()) {
            "help" -> {
                mensaje = minimessage.deserialize("$prefix <gold>Available commands:<newline>- <gray>/tmcore</gray><newline>- <gray>/tmcore get_player_by_uuid</gray><newline>- <gray>/tmcore get_uuid_by_player</gray><newline>- <gray>/joindate</gray><newline>- <gray>/playtime</gray><newline>- <gray>/lastseen</gray><newline>- <gray>/dupe</gray><newline>- <gray>/vote</gray><newline>- <gray>/seed</gray><newline>- <gray>/toggleannouncements</gray></gold>")
                sender.sendMessage(mensaje)
            }
            "reload" -> {
                reloadPlugin()
                isConfigUpdated()
                mensaje = minimessage.deserialize("$prefix <gold>Plugin reloaded.</gold>")
                sender.sendMessage(mensaje)
            }
            "get_uuid_by_player" -> {
                if (sender !is Player && args.size < 2) {
                    mensaje = minimessage.deserialize("$prefix <red>You need to specify a player to use this command.</red>")
                    sender.sendMessage(mensaje)
                    return true
                }

                val targetUser = if (args.size < 2) {
                    sender.name
                }
                else{
                    args[1]
                }
                Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {
                    val playerDataCache = File(playerDataPath, "${targetUser.lowercase()}.yml")
                    val offlineplayer = if (!playerDataCache.exists()) {
                        logToConsole("<red>$targetUser's UUID file doesn't exist.")
                        getOfflinePlayer(targetUser)
                    }
                    else{
                        val config = YamlConfiguration.loadConfiguration(playerDataCache)
                        val configUuid = config.getString("uuid")
                        if (configUuid == null) {
                            logToConsole("<yellow>UUID value is null.")
                            getOfflinePlayer(targetUser)
                        }
                        else{
                            val uuid = UUID.fromString(configUuid)
                            logToConsole("<green>UUID file found: ${uuid.toString()}")
                            getOfflinePlayer(uuid)
                        }
                    }

                    if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L) {
                        val mensaje = minimessage.deserialize("$prefix <red>This player has never entered the server or is not in the server cache.</red>")
                        sender.sendMessage(mensaje)
                        return@Runnable
                    }

                    val uuid = offlineplayer.uniqueId
                    val mensaje = minimessage.deserialize("$prefix <gold>$targetUser's UUID is <bold><click:copy_to_clipboard:$uuid>$uuid</click></bold>.</gold>")
                    sender.sendMessage(mensaje)
                })
            }
            "get_player_by_uuid" -> {
                if (args.size < 2) {
                    mensaje = minimessage.deserialize("$prefix <red>You need to specify an UUID to use this command.</red>")
                    sender.sendMessage(mensaje)
                    return true
                }

                val uuidString = args[1]
                val uuid: UUID = UUID.fromString(uuidString)

                val offlineplayer = getOfflinePlayer(uuid)

                if (!offlineplayer.isOnline && offlineplayer.firstPlayed == 0L) {
                    mensaje = minimessage.deserialize("$prefix <red>This player has never entered the server or is not in the server cache.</red>")
                    sender.sendMessage(mensaje)
                }
                else{
                    mensaje = minimessage.deserialize("$prefix <gold>The user is <bold>${offlineplayer.name}</bold>.</gold>")
                    sender.sendMessage(mensaje)
                }
            }
            /*"migrate_fallen_dupe" -> {
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
            }*/
            else -> {
                sender.sendMessage(minimessage.deserialize("$prefix <red>That command is not available.</red>"))
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            val subs = listOf("help", "reload", "get_uuid_by_player", "get_player_by_uuid")
            for (s in subs) {
                if (s.startsWith(args[0].lowercase())) {
                    completions.add(s)
                }
            }
        }
        return completions
    }
}