@file:Suppress("UnstableApiUsage")

package dev.slne.surf.servertour.dialogs.own.member

import dev.slne.surf.servertour.dialogs.own.buildOwnTourTitle
import dev.slne.surf.servertour.entry.EntryMember
import dev.slne.surf.servertour.entry.TourEntry
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase

fun buildMemberBody(entry: TourEntry, member: EntryMember) = buildText {
    variableKey("Beschreibung: ")
    appendNewline(2)
    variableValue(member.description?.ifBlank { "Keine Beschreibung vorhanden" }
        ?: "Keine Beschreibung vorhanden")
}

fun oneMemberDialog(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
): Dialog = dialog {
    base {
        title(
            buildOwnTourTitle(
                entry,
                buildText { spacer("Mitglieder") },
                member.asComponent()
            )
        )
        externalTitle {
            text(member.offlinePlayer.name ?: member.offlinePlayer.uniqueId.toString())
        }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                append(buildMemberBody(entry, member))
            }
        }
    }

    type {
        if (editable) {
            multiAction {
                columns(2)
                action(changeDescriptionButton(entry, member, editable))
                action(removeMemberButton(entry, member, editable))

                exitAction(backButton(entry, editable))
            }
        } else {
            notice(backButton(entry, editable))
        }
    }
}

private fun backButton(entry: TourEntry, editable: Boolean) = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den Mitgliedern") }

    action {
        playerCallback { it.showDialog(ownTourMembersDialog(entry, editable)) }
    }
}

private fun changeDescriptionButton(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
) = actionButton {
    label { text("Beschreibung ändern") }
    tooltip { info("Ändert die Beschreibung des Mitglieds") }

    action {
        playerCallback { it.showDialog(changeMemberDescriptionDialog(entry, member, editable)) }
    }
}

private fun removeMemberButton(
    entry: TourEntry,
    member: EntryMember,
    editable: Boolean
) =
    actionButton {
        label { error("Entfernen") }
        tooltip { error("Entfernt das Mitglied von der Einreichung") }

        action {
            playerCallback { it.showDialog(removeMemberDialog(entry, member, editable)) }
        }
    }
