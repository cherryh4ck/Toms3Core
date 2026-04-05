package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractIllegalListener(private val plugin: Toms3Core) : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item

        if (item != null && item.type.name.contains("SPAWN_EGG")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDispense(event: BlockDispenseEvent) {
        val item = event.item

        if (item.type.name.contains("SPAWN_EGG")){
            event.isCancelled = true
        }
    }
}