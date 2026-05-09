package io.github.Cherryh4ck.toms3Core.Listeners

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class OPCommandBlacklistListener(private val plugin: Toms3Core) : Listener {
    @EventHandler

    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val message = event.message.lowercase().drop(1)
        val base = message.split(" ")[0]
        val player = event.player

        if (player.isOp && plugin.op_blacklisted_commands.contains(base)) {
            event.isCancelled = true
            player.sendMessage(plugin.minimessage.deserialize("<red>This command is blocked.</red>"))
        }
    }
}