package dev.slne.surf.servertour.view

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.servertour.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class ViewListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }

        val player = event.player

        if (!viewManager.currentPoIViews.contains(player.uniqueId) && !viewManager.currentTourViews.contains(
                player.uniqueId
            )
        ) {
            return
        }

        event.isCancelled = true
        player.sendText {
            appendPrefix()
            error("Du kannst dich nicht bewegen, während du dir einen Eintrag ansiehst!")
            appendSpace()
            info("Sollte ein Fehler auftreten, kannst du /servertour exitView benutzen.")
        }
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val player = event.player

        plugin.launch {
            viewManager.quit(player)
        }
    }
}