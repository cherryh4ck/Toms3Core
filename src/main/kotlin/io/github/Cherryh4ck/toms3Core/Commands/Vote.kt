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
            val message = if (isSpanish) {
                plugin.minimessage.deserialize("<gold>Puedes votarnos yendo a <click:open_url:https://vote.toms3.cc><bold>vote.toms3.cc</bold></click>.<newline>Recuerda que votar no da recompensas, pero ayuda a la visibilidad del servidor.")
            } else {
                plugin.minimessage.deserialize("<gold>You can vote us by going to <click:open_url:https://vote.toms3.cc><bold>vote.toms3.cc</bold></click>.<newline>Remember that voting doesn't give any rewards, but helps the visibility of the server.")
            }
            sender.sendMessage(message)
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