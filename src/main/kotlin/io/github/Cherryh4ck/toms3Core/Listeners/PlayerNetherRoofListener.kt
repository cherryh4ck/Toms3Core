package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerNetherRoofListener(private val plugin: Toms3Core) : Listener {
    // Cheap patch for VClip travel exploit
    // also prevents players from being killed by the void damage thing on the nether roof
    @EventHandler

    fun onPlayerMove(event: PlayerMoveEvent) {
        val from = event.from
        val to = event.to ?: return

        if (from.blockY == to.blockY) {
            return
        }

        val player = event.player
        val playerLocale = player.locale().toString()
        val world = player.world

        if (world.environment == World.Environment.NETHER){
            if (to.y > 127){
                val loc = Location(world, to.x, 120.0, to.z)
                player.teleport(loc)
                if (playerLocale.startsWith("es")){
                    player.sendMessage(plugin.minimessage.deserialize("<gold>${plugin.prefix} El techo del Nether está deshabilitado."))
                }
                else{
                    player.sendMessage(plugin.minimessage.deserialize("<gold>${plugin.prefix} Nether Roof is currently disabled."))
                }
                plugin.sendError("${player.name} tried to access Nether Roof.")
            }
            else if (to.y < 0 && player.inventory.chestplate?.type == Material.ELYTRA){
                val loc = Location(world, to.x, 7.0, to.z)
                player.teleport(loc)
                if (playerLocale.startsWith("es")){
                    player.sendMessage(plugin.minimessage.deserialize("<gold>${plugin.prefix} No se pueden usar las elytras debajo de la bedrock debido a un exploit."))
                }
                else{
                    player.sendMessage(plugin.minimessage.deserialize("<gold>${plugin.prefix} This travel exploit is currently disabled."))
                }
                plugin.sendError("${player.name} tried to execute the VClip exploit.")
            }
        }
    }
}