package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File

class CachePlayerJoinListener(private val plugin: Toms3Core) : Listener {
    @EventHandler

    fun onPlayerJoin(event: PlayerJoinEvent){
        val player = event.player
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val playerData = File(plugin.playerDataPath, "${player.name}.yml")
            val config = YamlConfiguration.loadConfiguration(playerData)

            val getUuid = config.getString("uuid")

            if (getUuid == null || getUuid != player.uniqueId.toString()) {
                if (getUuid != null) {
                    plugin.logger.info("UUID file updated for ${player.name}.")
                }
                else{
                    plugin.logger.info("UUID file created for ${player.name}.")
                }
                config.set("uuid", player.uniqueId.toString())
                try {
                    config.save(playerData)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })
    }
}