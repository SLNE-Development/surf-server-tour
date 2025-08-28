package dev.slne.surf.servertour.commands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.servertour.plugin
import dev.slne.surf.servertour.view.viewManager
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.serverTourViewExitCommand() = subcommand("exitView") {
    playerExecutor { player, _ ->
        plugin.launch {
            val success = viewManager.exitView(player)

            if (success) {
                player.sendText {
                    appendPrefix()
                    success("Du hast die Ansicht verlassen und wurdest zurück teleportiert.")
                }
                return@launch
            }

            player.sendText {
                appendPrefix()
                error("Du befindest dich in keiner Ansicht.")
            }
        }
    }
}