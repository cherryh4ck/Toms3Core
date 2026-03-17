package io.github.Cherryh4ck.toms3Core

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val plugin: Toms3Core) : Listener {
    // motd thing

    @EventHandler

    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerLocale = player.locale().toString()

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (playerLocale.startsWith("es")){
                player.sendMessage(plugin.minimessage.deserialize(plugin.motd_es.toString()))
            }
            else {
                player.sendMessage(plugin.minimessage.deserialize(plugin.motd_general.toString()))
            }
        }, 20L)
    }
}