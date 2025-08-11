@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own

import dev.slne.surf.servertour.dialogs.listOwnToursDialog
import dev.slne.surf.servertour.dialogs.own.information.descriptionOwnTourEntryDialog
import dev.slne.surf.servertour.dialogs.own.information.renameOwnTourEntryDialog
import dev.slne.surf.servertour.dialogs.own.member.addMemberToOwnTourDialog
import dev.slne.surf.servertour.dialogs.own.member.ownTourMembersDialog
import dev.slne.surf.servertour.dialogs.own.poi.ownTourPoisDialog
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
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
                if (offlineOwner.hasPlayedBefore()) {
                    variableValue(offlineOwner.name ?: "Unbekannt")
                } else {
                    variableValue("Unbekannt")
                }
                appendNewline(2)

                variableKey("Mitglieder: ")
                variableValue(entry.members.size)
                appendNewline(2)

                variableKey("Beschreibung:")
                appendNewline()
                variableValue(entry.description.ifBlank { "Keine Beschreibung vorhanden" })
            }
        }
    }

    type {
        multiAction {
            columns(2)

            action(renameButton(entry))
            action(descriptionButton(entry))

            action(listMembersButton(entry))
            action(addMemberButton(entry))

            action(listPoisButton(entry))
            action(addPoiButton(entry))

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

private fun listPoisButton(entry: TourEntry) = actionButton {
    label { text("POIs") }
    tooltip { info("Zeige die POIs der Einreichung an") }

    action {
        playerCallback { it.showDialog(ownTourPoisDialog(entry)) }
    }
}

@OptIn(NmsUseWithCaution::class)
private fun addPoiButton(entry: TourEntry) = actionButton {
    label { text("POI hinzufügen") }
    tooltip { info("Füge einen POI zu deiner Einreichung hinzu") }

    action {
        playerCallback {
            it.clearDialogs(true)
        }
    }
}

private fun listMembersButton(entry: TourEntry) = actionButton {
    label { text("Mitglieder") }
    tooltip { info("Zeige die Mitglieder der Einreichung an") }

    action {
        playerCallback { it.showDialog(ownTourMembersDialog(entry)) }
    }
}

private fun addMemberButton(entry: TourEntry) = actionButton {
    label { text("Mitglied hinzufügen") }
    tooltip { info("Füge ein Mitglied zu deiner Einreichung hinzu") }

    action {
        playerCallback { it.showDialog(addMemberToOwnTourDialog(entry)) }
    }
}

private fun renameButton(entry: TourEntry) = actionButton {
    label { text("Name") }
    tooltip { info("Benenne deine Einreichung um") }

    action {
        playerCallback { it.showDialog(renameOwnTourEntryDialog(entry)) }
    }
}

private fun descriptionButton(entry: TourEntry) = actionButton {
    label { text("Beschreibung") }
    tooltip { info("Ändere die Beschreibung der Einreichung") }

    action {
        playerCallback { it.showDialog(descriptionOwnTourEntryDialog(entry)) }
    }
}