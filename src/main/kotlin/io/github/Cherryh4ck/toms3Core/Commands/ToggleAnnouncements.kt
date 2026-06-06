package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class ToggleAnnouncements(private val plugin : Toms3Core) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player){
            val locale = sender.locale().toString()
            val isSpanish = locale.startsWith("es")
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                val playerData = File(plugin.playerDataPath, "${sender.name.lowercase()}.yml")
                val config = YamlConfiguration.loadConfiguration(playerData)
                val toggle = config.getBoolean("toggle-announcements")
                config.set("toggle-announcements", !toggle)
                try {
                    config.save(playerData)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    val message: Component
                    if (toggle) {
                        plugin.haveAnnouncementsDisabled.add(sender.uniqueId)
                        message = if (isSpanish && plugin.spanish_enabled) {
                            plugin.minimessage.deserialize("${plugin.prefix} ${plugin.toggleannouncements_message_off_es}")
                        } else {
                            plugin.minimessage.deserialize("${plugin.prefix} ${plugin.toggleannouncements_message_off_en}")
                        }
                    } else {
                        plugin.haveAnnouncementsDisabled.remove(sender.uniqueId)
                        message = if (isSpanish && plugin.spanish_enabled) {
                            plugin.minimessage.deserialize("${plugin.prefix} ${plugin.toggleannouncements_message_on_es}")
                        } else {
                            plugin.minimessage.deserialize("${plugin.prefix} ${plugin.toggleannouncements_message_on_en}")
                        }
                    }
                    sender.sendMessage(message)
                })
            })
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