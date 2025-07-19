@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import dev.slne.surf.servertour.dialogs.ownTourDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun ownTourMembersDialog(entry: TourEntry): Dialog = dialog {
    base {
        title { info("Mitglieder ${entry.name}") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                variableKey("Mitglieder: ")
                variableValue(entry.members.size)
            }
        }
    }

    type {
        dialogList {
            columns(3)
            buttonWidth(300)
            addAll(buildDialogList(entry))
            exitAction(backButton(entry))
        }
    }
}

private fun backButton(entry: TourEntry) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourDialog(entry))
        }
    }
}

private fun buildDialogList(entry: TourEntry) = entry.members.map { oneMemberDialog(entry, it) }

