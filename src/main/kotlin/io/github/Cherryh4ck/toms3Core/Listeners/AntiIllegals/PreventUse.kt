package io.github.Cherryh4ck.toms3Core.Listeners.AntiIllegals

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDispenseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class PreventUse(private val plugin: Toms3Core) : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val player = event.player

        if (isIllegal(item)) {
            // Not that necessary in there, but just in case
            val clicked = event.clickedBlock
            if (clicked?.type == Material.END_PORTAL_FRAME && item.type == Material.ENDER_EYE) {
                return
            }

            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDispense(event: BlockDispenseEvent) {
        val item = event.item

        if (isIllegal(item)) {
            event.isCancelled = true
        }
    }

    fun isIllegal(item: ItemStack) : Boolean {
        return plugin.illegals_prevent_use.any { forbidden ->
            item.type.name.contains(forbidden)
        }
    }
}