package io.github.Cherryh4ck.toms3Core.Commands

import io.github.Cherryh4ck.toms3Core.DiscordWebhook
import io.github.Cherryh4ck.toms3Core.Toms3Core
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File

class Dupe(private val plugin : Toms3Core) : TabExecutor {
    val minimessage = MiniMessage.miniMessage()
    val drunkness = PotionEffect(PotionEffectType.NAUSEA, 30 * 20, 0, false, true, true)
    val darkness = PotionEffect(PotionEffectType.DARKNESS, 60 * 20, 0, false, true, true)
    val poison = PotionEffect(PotionEffectType.POISON, 120 * 20, 2, false, true, true)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            val locale = sender.locale().toString()
            val isSpanish = locale.startsWith("es") // le isspanish
            val playerDataCache = File(plugin.playerDataPath, "${sender.name}.yml")
            val config = YamlConfiguration.loadConfiguration(playerDataCache)
            val alreadyFallen = config.getBoolean("fallen-for-dupe")

            if (alreadyFallen) {
                if (isSpanish) {
                    sender.sendMessage(minimessage.deserialize("${plugin.prefix} <red>No seas idiota, newfag.</red>"))
                }
                else {
                    sender.sendMessage(minimessage.deserialize("${plugin.prefix} <red>Don't be an idiot, newfag.</red>"))
                }
                return true
            }

            if (plugin.discordWebhook.toString().isNotEmpty()){
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                    DiscordWebhook.sendDiscordWebhook(plugin.discordWebhook.toString(), "**${sender.name}** tried to use the /dupe command. what a fucking retard.")
                })
            }

            val joinMessage = if (isSpanish) { minimessage.deserialize("<gray>popbob se unió al servidor.</gray>") } else { minimessage.deserialize("<gray>popbob has joined the server.</gray>") }
            val hallOfShameMessage = if (isSpanish) { minimessage.deserialize("<red>Ahora estás en la lista de jugadores lamentables.</red>") } else { minimessage.deserialize("<red>You have been added to the Hall of Shame.</red>") }
            sender.sendMessage(joinMessage)
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                sender.sendMessage("${sender.name} » popbob my coords are ${sender.x.toInt().toString()} ${sender.z.toInt().toString()} come get me please")
            }, 20L)
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                sender.sendMessage("popbob » ur fucked, im coming")
                val health = sender.health - 1.0
                sender.damage(health)
                sender.addPotionEffect(drunkness)
                sender.addPotionEffect(darkness)
                sender.addPotionEffect(poison)
                sender.playSound(sender.location, Sound.AMBIENT_CAVE, 2.0f, 0.5f)
                sender.playSound(sender.location, Sound.ITEM_TOTEM_USE, 2.0f, 0.5f)
            }, 50L)
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                val world = Bukkit.getWorld(sender.world.name)
                val location = Location(world, sender.location.x, sender.location.y, sender.location.z)
                world?.strikeLightning(location)
                sender.sendMessage(hallOfShameMessage)
                sender.playSound(sender.location, Sound.ENTITY_WITHER_SPAWN, 2.0f, 0.15f)
                Bukkit.getOnlinePlayers().forEach { player ->
                    val locale = player.locale().toString()
                    if (locale.startsWith("es")) {
                        player.sendMessage(minimessage.deserialize("<gray>${sender.name} fue agregado a la <light_purple>lista de jugadores miserables</light_purple>."))
                    }
                    else{
                        player.sendMessage(minimessage.deserialize("<gray>${sender.name} has been added to the <light_purple>Hall of Shame</light_purple>."))
                    }
                }
            }, 100L)

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                config.set("fallen-for-dupe", true)
                try {
                    config.save(playerDataCache)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            })
        }
        else{
            plugin.sendError("You must be a player to execute this command.")
        }
        return true
    }

    override fun onTabComplete(p0: CommandSender, p1: Command, p2: String, p3: Array<out String> ): List<String?>? {
        return emptyList()
    }
}