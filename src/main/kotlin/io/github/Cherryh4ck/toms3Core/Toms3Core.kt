package io.github.Cherryh4ck.toms3Core

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Toms3Core : JavaPlugin() {
    val minimessage = MiniMessage.miniMessage()
    val prefix = "<gold>[<red><bold>Toms3<white>Core</white></bold></red>]"

    override fun onEnable() {
        logger.info("---------------------")
        logger.info("Core activado.")
        logger.info("---------------------")

        getCommand("tmcore")?.setExecutor(this)
        getCommand("tmcore")?.setTabCompleter(this)
        getCommand("playtime")?.setExecutor(Playtime(this))
        getCommand("dupe")?.setExecutor(DupeJoke(this))
        getCommand("joindate")?.setExecutor(Joindate(this))
        getCommand("jd")?.setExecutor(Joindate(this))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun sendError(message : String){
        logger.warning(message)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val mensaje : Component
        if(args.isEmpty()){
            mensaje = minimessage.deserialize("<gold>$prefix Plugin corriendo - versión 1.0 (revisión n. 1)</gold>")
            sender.sendMessage(mensaje)
            return true
        }

        when (args[0].lowercase()) {
            "help" -> {
                mensaje = minimessage.deserialize("$prefix <gold>Comandos disponibles:<newline>- <gray>/tmcore</gray><newline>- <gray>/tmcore illegal_test</gray><newline>- <gray>/joindate</gray><newline>- <gray>/playtime</gray></gold>")
                sender.sendMessage(mensaje)
            }
            "illegal_test" -> {
                // terminar
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
            val subs = listOf("help", "illegal_test")
            for (s in subs) {
                if (s.startsWith(args[0].lowercase())) {
                    completions.add(s)
                }
            }
        }
        return completions
    }
}
