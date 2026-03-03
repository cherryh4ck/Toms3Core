package io.github.Cherryh4ck.toms3Core

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DupeJoke(private val plugin : Toms3Core) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            sender.sendMessage("${sender.name} » my coords are ${sender.x.toInt().toString()} ${sender.z.toInt().toString()} come get me")
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                sender.sendMessage("popbob » ur fucked, im coming")
            }, 40L)
        }
        else{
            plugin.sendError("Debes ser un jugador para ejecutar este comando.")
        }
        return true
    }
}