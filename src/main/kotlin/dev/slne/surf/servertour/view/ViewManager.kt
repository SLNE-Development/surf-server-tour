package dev.slne.surf.servertour.view

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.servertour.entry.Poi
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.servertour.plugin
import dev.slne.surf.servertour.utils.setOfflineGameMode
import dev.slne.surf.servertour.utils.setOfflineLocation
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit

class ViewManager {
    val currentTourViews = mutableObject2ObjectMapOf<UUID, Pair<TourEntry, Long>>()
    val currentPoIViews = mutableObject2ObjectMapOf<UUID, Pair<Poi, Long>>()
    val previousLocations = mutableObject2ObjectMapOf<UUID, Location>()

    lateinit var task: ScheduledTask

    suspend fun viewPoi(player: Player, poi: Poi) =
        withContext(plugin.regionDispatcher(poi.location)) {
            currentPoIViews[player.uniqueId] = poi to System.currentTimeMillis()
            previousLocations[player.uniqueId] = player.location

            player.gameMode = GameMode.SPECTATOR
            player.teleportAsync(poi.location)
        }

    suspend fun viewTour(player: Player, entry: TourEntry) =
        withContext(plugin.regionDispatcher(entry.location)) {
            currentTourViews[player.uniqueId] = entry to System.currentTimeMillis()
            previousLocations[player.uniqueId] = player.location

            player.gameMode = GameMode.SPECTATOR
            player.teleportAsync(entry.location)
        }


    suspend fun exitViewPoi(player: Player) {
        val loc = previousLocations.remove(player.uniqueId)
            ?: error("No previous location found for player ${player.name}")
        currentPoIViews.remove(player.uniqueId)

        withContext(plugin.regionDispatcher(loc)) {
            player.teleportAsync(loc)
            player.gameMode = GameMode.SURVIVAL
        }
    }

    suspend fun exitViewTour(player: Player) {
        val loc = previousLocations.remove(player.uniqueId)
            ?: error("No previous location found for player ${player.name}")
        currentTourViews.remove(player.uniqueId)

        withContext(plugin.regionDispatcher(loc)) {
            player.teleportAsync(loc)
            player.gameMode = GameMode.SURVIVAL
        }
    }

    suspend fun quitSync(player: Player) {
        if (currentPoIViews.containsKey(player.uniqueId)) {
            val loc = previousLocations.remove(player.uniqueId)
                ?: error("No previous location found for player ${player.name}")
            currentPoIViews.remove(player.uniqueId)

            player.setOfflineGameMode(GameMode.SURVIVAL)
            player.setOfflineLocation(loc)
        } else if (currentTourViews.containsKey(player.uniqueId)) {
            val loc = previousLocations.remove(player.uniqueId)
                ?: error("No previous location found for player ${player.name}")
            currentTourViews.remove(player.uniqueId)

            player.setOfflineGameMode(GameMode.SURVIVAL)
            player.setOfflineLocation(loc)
        }
    }

    suspend fun shutdown() {
        currentPoIViews.keys.forEach { uuid ->
            val player = plugin.server.getPlayer(uuid) ?: return@forEach
            exitViewPoi(player)
        }

        currentTourViews.keys.forEach { uuid ->
            val player = plugin.server.getPlayer(uuid) ?: return@forEach
            exitViewTour(player)
        }

        task.cancel()
    }

    suspend fun exitView(player: Player): Boolean {
        if (currentPoIViews.containsKey(player.uniqueId)) {
            exitViewPoi(player)
            return true
        } else if (currentTourViews.containsKey(player.uniqueId)) {
            exitViewTour(player)
            return true
        }

        return false
    }

    fun startTask() {
        task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            val now = System.currentTimeMillis()

            currentTourViews.entries.toList().forEach { (uuid, pair) ->
                val player = plugin.server.getPlayer(uuid) ?: return@forEach
                val remaining = (pair.second + TimeUnit.SECONDS.toMillis(5) - now) / 1000

                if (remaining <= 0) {
                    plugin.launch {
                        exitViewTour(player)
                    }
                } else {
                    player.sendActionBar(buildText {
                        info("Du wirst in ")
                        variableValue("$remaining")
                        info(" Sekunden zurück teleportiert")
                    })
                }
            }

            currentPoIViews.entries.toList().forEach { (uuid, pair) ->
                val player = plugin.server.getPlayer(uuid) ?: return@forEach
                val remaining = (pair.second + TimeUnit.SECONDS.toMillis(5) - now) / 1000

                if (remaining <= 0) {
                    plugin.launch {
                        exitViewPoi(player)
                    }
                } else {
                    player.sendActionBar(buildText {
                        info("Du wirst in ")
                        variableValue("$remaining")
                        info(" Sekunden zurück teleportiert")
                    })
                }
            }

        }, 0L, 1L, TimeUnit.SECONDS)
    }


    companion object {
        val INSTANCE = ViewManager()
    }
}

val viewManager get() = ViewManager.INSTANCE