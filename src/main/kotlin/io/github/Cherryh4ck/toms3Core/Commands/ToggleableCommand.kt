package io.github.Cherryh4ck.toms3Core.Commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ToggleableCommand(name: String, aliases: List<String>, private val executor: TabExecutor) : Command(name, "", "/$name", aliases) {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        return executor.onCommand(sender, this, commandLabel, args)
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String> {
        return executor.onTabComplete(sender, this, alias, args) ?: emptyList()
    }
}