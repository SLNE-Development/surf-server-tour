package dev.slne.surf.servertour.commands.subcommands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.booleanArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.servertour.entry.EntryManager
import dev.slne.surf.servertour.entry.FakeDataManager
import dev.slne.surf.servertour.plugin
import dev.slne.surf.servertour.utils.ServerTourPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.serverTourFakeDataCommand() = subcommand("fake-data") {
    withPermission(ServerTourPermissionRegistry.FAKE_CACHED_DATA)

    booleanArgument("clear", true)

    playerExecutor { player, args ->
        val clear: Boolean? by args

        plugin.launch {
            if (clear == true) {
                EntryManager.deleteAll()
                EntryManager.clearCache()

                player.sendText {
                    appendPrefix()

                    success("Die Daten wurden geleert")
                }
            }

            FakeDataManager.fakeData(player.uniqueId)

            player.sendText {
                appendPrefix()

                success("Die Daten wurden gefälscht")
            }
        }
    }
}