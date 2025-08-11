@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun oneMemberDialog(entry: TourEntry, member: EntryMember): Dialog = dialog {
    base {
        title(member.asComponent())
        externalTitle {
            text(member.offlinePlayer.name ?: member.offlinePlayer.uniqueId.toString())
        }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                variableKey("Beschreibung: ")
                appendNewline(2)
                variableValue(member.description?.ifBlank { "Keine Beschreibung vorhanden" }
                    ?: "Keine Beschreibung vorhanden")
            }
        }
    }

    type {
        multiAction {
            action(changeDescriptionButton(entry, member))
            action(removeMemberButton(entry, member))

            exitAction {
                action {
                    label { text("Zurück") }
                    tooltip { info("Zurück zu den Mitgliedern") }
                    playerCallback { it.showDialog(ownTourMembersDialog(entry)) }
                }
            }
        }
    }
}

private fun changeDescriptionButton(entry: TourEntry, member: EntryMember) = actionButton {
    label { text("Beschreibung ändern") }
    tooltip { info("Ändert die Beschreibung des Mitglieds") }

    action {
        playerCallback { it.showDialog(changeMemberDescriptionDialog(entry, member)) }
    }
}

private fun removeMemberButton(entry: TourEntry, member: EntryMember) = actionButton {
    label { error("Entfernen") }
    tooltip { error("Entfernt das Mitglied von der Einreichung") }

    action {
        playerCallback { it.showDialog(removeMemberDialog(entry, member)) }
    }
}
