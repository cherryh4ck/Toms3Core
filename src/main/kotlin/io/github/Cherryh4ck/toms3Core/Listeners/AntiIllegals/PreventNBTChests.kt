package io.github.Cherryh4ck.toms3Core.Listeners.AntiIllegals

import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

class PreventNBTChests : Listener {
    fun checkItem(item: ItemStack) : Boolean{
        if (item.type != Material.CHEST && item.type != Material.TRAPPED_CHEST) {
            return false
        }
        if (!item.hasItemMeta()){
            return false
        }

        val meta = item.itemMeta
        if (meta is BlockStateMeta) {
            val state = meta.blockState as? Chest ?: return false
            val inv = state.inventory
            val count = inv.contents.filterNotNull().size
            if (count > 0){
                return true
            }
        }
        return false
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val cursor = event.cursor
        if (cursor.type != Material.AIR && checkItem(cursor)) {
            event.isCancelled = true
            event.whoClicked.setItemOnCursor(null)
            return
        }

        val clickedInv = event.clickedInventory ?: return
        val item = clickedInv.getItem(event.slot) ?: return
        if (checkItem(item)) {
            event.isCancelled = true
            clickedInv.setItem(event.slot, null)
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        for ((_, item) in event.newItems) {
            if (checkItem(item)) {
                event.isCancelled = true
                event.whoClicked.setItemOnCursor(null)
                return
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (checkItem(event.itemInHand)) {
            event.isCancelled = true
            event.player.inventory.setItem(event.hand, null)
        }
    }
}