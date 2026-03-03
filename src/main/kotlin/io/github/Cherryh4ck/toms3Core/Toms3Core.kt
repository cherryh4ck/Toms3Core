package io.github.Cherryh4ck.toms3Core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Toms3Core : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()
    val prefix = "<gold>[<red><bold>Toms3Core</bold></red>]"

    override fun onEnable() {
        logger.info("---------------------")
        logger.info("Core activado.")
        logger.info("---------------------")

        getCommand("playtime")?.setExecutor(Playtime(this))
        getCommand("dupe")?.setExecutor(DupeJoke(this))
        getCommand("tmcore")?.setExecutor(this)
        getCommand("tmcore")?.setTabCompleter(this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sendError(message : String){
        logger.warning(message)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(args.isEmpty()){
            val mensaje = minimessage.deserialize("<gold>$prefix Plugin corriendo - versión 1.0</gold>")
            sender.sendMessage(mensaje)
            return true
        }

        when (args[0].lowercase()) {
            "illegal_test" -> {
                // terminar
                val mensaje : Component
                if (sender is Player) {
                    mensaje = minimessage.deserialize("$prefix <red>popbob</red>")
                }
                else{
                    mensaje = minimessage.deserialize("$prefix <red>Debes ser un jugador para usar este comando.</red>")
                }
                sender.sendMessage(mensaje)
            }

            else -> {
                // por qué no uso senderror? pues ni idea xD
                val mensaje = minimessage.deserialize("$prefix <red>Ese comando no existe.</red>")
                sender.sendMessage(mensaje)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        val completions = mutableListOf<String>()
        if (args.size == 1) {
            val subs = listOf("illegal_test")
            for (s in subs) {
                if (s.startsWith(args[0].lowercase())) {
                    completions.add(s)
                }
            }
        }

        return completions
    }
}
