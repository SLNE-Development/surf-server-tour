@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs

import dev.slne.surf.servertour.dialogs.own.descriptionOwnTourEntryDialog
import dev.slne.surf.servertour.dialogs.own.member.addMemberToOwnTourDialog
import dev.slne.surf.servertour.dialogs.own.member.ownTourMembersDialog
import dev.slne.surf.servertour.dialogs.own.renameOwnTourEntryDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
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

                val offlineOwner = entry.owner.offlinePlayer
                if (offlineOwner.hasPlayedBefore() == true) {
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
            action(listMembersButton(entry))
            action(addMemberButton(entry))

            exitAction {
                label { text("Zurück") }
                tooltip { info("Zurück zu den Einreichungen") }

                action {
                    playerCallback {
                        it.showDialog(listOwnToursDialog(entry.owner.uuid))
                    }
                }
            }
        }
    }
}

private fun listMembersButton(entry: TourEntry) = actionButton {
    label { text("Mitglieder") }
    tooltip { info("Zeige die Mitglieder der Einreichung") }

    action {
        playerCallback { player ->
            player.showDialog(ownTourMembersDialog(entry))
        }
    }
}

private fun addMemberButton(entry: TourEntry) = actionButton {
    label { text("Mitglied hinzufügen") }
    tooltip { info("Füge ein Mitglied zu deiner Einreichung hinzu") }

    action {
        playerCallback { player ->
            player.showDialog(addMemberToOwnTourDialog(entry))
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