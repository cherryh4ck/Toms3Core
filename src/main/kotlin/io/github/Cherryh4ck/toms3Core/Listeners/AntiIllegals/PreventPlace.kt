package io.github.Cherryh4ck.toms3Core.Listeners.AntiIllegals

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class PreventPlace(private val plugin : Toms3Core) : Listener {
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.block
        if (plugin.illegals_prevent_place.contains(block.type.name)){
            if (block.type == Material.END_PORTAL_FRAME && event.itemInHand.type == Material.ENDER_EYE) {
                return
            }

            event.isCancelled = true
        }
    }
}