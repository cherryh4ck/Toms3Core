package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import kotlin.random.Random

class Seed(private val plugin : Toms3Core) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player){
            val randomSeed =  Random.nextLong()
            val seedComponent = Component.text("$randomSeed")
                .color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click")))
                .clickEvent(ClickEvent.copyToClipboard(randomSeed.toString()))
            val formatted = Component.text("[")
                .append(seedComponent)
                .append(Component.text("]"))

            sender.sendMessage(Component.translatable("commands.seed.success", formatted))
        }
        else{
            plugin.logToConsole("<red>You must be a player to execute this command.")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        return emptyList()
    }
}