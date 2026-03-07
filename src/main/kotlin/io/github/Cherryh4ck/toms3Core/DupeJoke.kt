package io.github.Cherryh4ck.toms3Core

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class DupeJoke(private val plugin : Toms3Core) : TabExecutor {
    val minimessage = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val locale = sender.locale().toString()
            val isSpanish = locale.startsWith("es") // le isspanish
            val alreadyFallen = plugin.config.getStringList("fallen-for-dupe") ?: mutableListOf()

            if (alreadyFallen.contains(sender.uniqueId.toString())) {
                if (isSpanish) { sender.sendMessage(minimessage.deserialize("<red>No seas idiota, newfag.</red>")) } else { sender.sendMessage(minimessage.deserialize("<red>Don't be an idiot, newfag.</red>")) }
                return true
            }

            val joinMessage = if (isSpanish) { minimessage.deserialize("<gray>popbob se unió al servidor.</gray>") } else { minimessage.deserialize("<gray>popbob has joined the server.</gray>") }
            sender.sendMessage(joinMessage)
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                sender.sendMessage("${sender.name} » popbob my coords are ${sender.x.toInt().toString()} ${sender.z.toInt().toString()} come get me please")
            }, 20L)
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                sender.sendMessage("popbob » ur fucked, im coming")
            }, 50L)

            alreadyFallen.add(sender.uniqueId.toString())
            plugin.config.set("fallen-for-dupe", alreadyFallen)
            plugin.saveConfig()
        }
        else{
            plugin.sendError("Debes ser un jugador para ejecutar este comando.")
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String> ): List<String?>? {
        return emptyList()
    }
}