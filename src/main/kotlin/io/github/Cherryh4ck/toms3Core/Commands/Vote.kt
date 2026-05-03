package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.Toms3Core
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class Vote(private val plugin : Toms3Core) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val locale = sender.locale().toString()
            val isSpanish = locale.startsWith("es")
            val message = if (isSpanish && plugin.spanish_enabled) {
                plugin.minimessage.deserialize("${plugin.prefix} ${plugin.vote_message_es}")
            } else {
                plugin.minimessage.deserialize("${plugin.prefix} ${plugin.vote_message_en}")
            }
            sender.sendMessage(message)
        }
        else{
            plugin.logToConsole("<red>You must be a player to use this command.")
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String> ): List<String?>? {
        return emptyList()
    }
}