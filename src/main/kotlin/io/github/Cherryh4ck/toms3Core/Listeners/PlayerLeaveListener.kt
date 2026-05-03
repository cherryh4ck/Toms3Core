package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File

class PlayerLeaveListener(private val plugin : Toms3Core) : Listener {
    @EventHandler

    fun onPlayerQuit(event: PlayerQuitEvent){
        val player = event.player
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val playerData = File(plugin.playerDataPath, "${player.name.lowercase()}.yml")
            val config = YamlConfiguration.loadConfiguration(playerData)
            val now = System.currentTimeMillis()
            config.set("last-seen", now)
            try {
                config.save(playerData)
                plugin.logToConsole("<green>Last seen date updated for ${player.name}.")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        })
    }
}