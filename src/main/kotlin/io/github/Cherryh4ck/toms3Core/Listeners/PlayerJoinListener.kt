package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
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
        val hasPlayedBefore = player.hasPlayedBefore()

        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (hasPlayedBefore) {
                if (playerLocale.startsWith("es") && plugin.spanish_enabled){
                    player.sendMessage(plugin.minimessage.deserialize(plugin.motd_es.toString()))
                    player.showTitle(Title.title(plugin.minimessage.deserialize(plugin.title_announcement_es.toString()), Component.empty()))
                    player.sendActionBar(plugin.minimessage.deserialize(plugin.actionbar_announcement_es.toString()))
                }
                else {
                    player.sendMessage(plugin.minimessage.deserialize(plugin.motd_general.toString()))
                    player.showTitle(Title.title(plugin.minimessage.deserialize(plugin.title_announcement_en.toString()), Component.empty()))
                    player.sendActionBar(plugin.minimessage.deserialize(plugin.actionbar_announcement_en.toString()))
                }
            }
            else{
                if (playerLocale.startsWith("es")){
                    player.sendMessage(plugin.minimessage.deserialize(plugin.first_join_motd_es.toString()))
                    player.showTitle(Title.title(plugin.minimessage.deserialize(plugin.title_announcement_es.toString()), Component.empty()))
                    player.sendActionBar(plugin.minimessage.deserialize(plugin.actionbar_announcement_es.toString()))
                }
                else {
                    player.sendMessage(plugin.minimessage.deserialize(plugin.first_join_motd.toString()))
                    player.showTitle(Title.title(plugin.minimessage.deserialize(plugin.title_announcement_en.toString()), Component.empty()))
                    player.sendActionBar(plugin.minimessage.deserialize(plugin.actionbar_announcement_en.toString()))
                }
            }
        }, 20L)
    }
}