@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs

import dev.slne.surf.servertour.dialogs.own.descriptionOwnTourEntryDialog
import dev.slne.surf.servertour.dialogs.own.renameOwnTourEntryDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun ownTourDialog(entry: TourEntry): Dialog = dialog {
    base {
        title { primary(entry.name) }
        externalTitle { text(entry.name) }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            item {
                item(entry.icon)
                simpleDescription(400) {
                    variableValue(entry.name)
                }
            }
            plainMessage(400) {
                variableKey("Ersteller: ")

                val offlineOwner = server.getOfflinePlayer(entry.owner)
                if (offlineOwner.hasPlayedBefore()) {
                    variableValue(offlineOwner.name ?: "Unbekannt")
                } else {
                    variableValue("Unbekannt")
                }
            }
            plainMessage(400) {
                variableKey("Mitglieder: ")
                variableValue(entry.members.size)
            }
            plainMessage(400) {
                variableKey("Beschreibung:")
            }
            plainMessage(600) {
                variableValue(entry.description.ifBlank { "Keine Beschreibung vorhanden" })
            }
        }
    }

    type {
        multiAction {
            action(renameButton(entry))
            action(descriptionButton(entry))

            exitAction {
                label { text("Zurück") }
                tooltip { info("Zurück zu den Einreichungen") }

                action {
                    playerCallback {
                        it.showDialog(listOwnToursDialog(entry.owner))
                    }
                }
            }
        }
    }
}

private fun renameButton(entry: TourEntry) = actionButton {
    label { text("Name") }
    tooltip { info("Benenne deine Einreichung um") }

    action {
        playerCallback { player ->
            player.showDialog(renameOwnTourEntryDialog(entry))
        }
    }
}

private fun descriptionButton(entry: TourEntry) = actionButton {
    label { text("Beschreibung") }
    tooltip { info("Ändere die Beschreibung der Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(descriptionOwnTourEntryDialog(entry))
        }
    }
}