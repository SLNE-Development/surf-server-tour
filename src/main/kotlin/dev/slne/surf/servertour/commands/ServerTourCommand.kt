package dev.slne.surf.servertour.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.servertour.dialogs.serverTourDialog
import dev.slne.surf.servertour.utils.ServerTourPermissionRegistry

fun serverTourCommand() = commandAPICommand("servertour") {
    withPermission(ServerTourPermissionRegistry.BASE)

    serverTourViewExitCommand()

    playerExecutor { player, _ ->
        player.showDialog(serverTourDialog(player.uniqueId))
    }
}