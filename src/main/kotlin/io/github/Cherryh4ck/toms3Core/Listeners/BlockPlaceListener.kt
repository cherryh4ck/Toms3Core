package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceListener(private val plugin: Toms3Core) : Listener {
    // This is only for shulkers and any class of chests
    @EventHandler

    fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.block
        val chunk = block.chunk

        if (isATileEntity(block.type) && plugin.chunklimits_enable){
            val count = chunk.tileEntities.size
            val maxCount = plugin.tile_entities_limit

            if (count > maxCount){
                event.isCancelled = true

                val player = event.player

                if (plugin.tile_entities_log){
                    val chunkX = chunk.x
                    val chunkZ = chunk.z
                    val regionX = chunkX shr 5
                    val regionZ = chunkZ shr 5
                    val chunkFileName = "r.$regionX.$regionZ.mca"
                    plugin.logToConsole("<yellow>Tile entity limit exceeded by ${player.name} at X: ${block.location.block.x} Y: ${block.location.block.y} Z: ${block.location.block.z} (World: ${chunk.world.name}) (Chunk: $chunkFileName)")
                }

                val isSpanish = event.player.locale().toString().startsWith("es")
                if (isSpanish){
                    player.sendMessage(plugin.minimessage.deserialize("<gold>${plugin.prefix} Las entidades de bloque están limitadas a $maxCount por chunk."))
                }
                else{
                    player.sendMessage(plugin.minimessage.deserialize("<gold>${plugin.prefix} Tile entities are limited to $maxCount per chunk."))
                }
            }
            return
        }

        if (plugin.illegals_prevent_place.contains(block.type.name) && plugin.illegals_enable){
            event.isCancelled = true
        }
    }

    fun isATileEntity(material: Material): Boolean {
        return material.isBlock && material.name.contains("CHEST") ||
                material.name.contains("SHULKER_BOX") ||
                material.name.contains("DISPENSER") ||
                material.name.contains("FURNACE") ||
                material.name.contains("BARREL") ||
                material.name.contains("DROPPER")
    }
}